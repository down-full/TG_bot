package org.example.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean; //позволяет сравнивать быстрее за счет исп. процессора


public class NotService {
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final ConcurrentMap<Long, ConcurrentMap<String, Threshold>> userThresholds = new ConcurrentHashMap<>();
    private static final long POLL_INTERVAL_SECONDS = 3600;
    private static final AtomicBoolean started = new AtomicBoolean(false);

    private static volatile TelegramLongPollingBot botInstance = null;

    public static void setBotInstance(TelegramLongPollingBot bot) {
        botInstance = bot;
    }

    public static class Threshold { // объект порога
        public enum Type { GREATER_EQUAL, LESS_EQUAL }
        public final Type type;
        public final double value;

        public Threshold(Type type, double value) {
            this.type = type;
            this.value = value;
        }

        public boolean matches(double rate) {
            if (type == Type.GREATER_EQUAL) return rate >= value;
            else return rate <= value;
        }

        @Override
        public String toString() {
            return (type == Type.GREATER_EQUAL ? ">=" : "<=") + value;
        }
    }

    public static void start() {
        if (started.compareAndSet(false, true)) {
            scheduler.scheduleAtFixedRate(NotService::pollAndNotify, 60, POLL_INTERVAL_SECONDS, TimeUnit.SECONDS);
        }
    }

    public static void shutdown() {
        scheduler.shutdownNow();
    }

    public static Map<String, Threshold> getUserThresholds(Long chatId) {
        return userThresholds.getOrDefault(chatId, new ConcurrentHashMap<>());
    }

    public static void setThreshold(Long chatId, String currency, Threshold threshold) {
        userThresholds.computeIfAbsent(chatId, id -> new ConcurrentHashMap<>()).put(currency, threshold);
    }

    public static void removeThreshold(Long chatId, String currency) {
        Map<String, Threshold> map = userThresholds.get(chatId);
        if (map != null) {
            map.remove(currency);
            if (map.isEmpty()) userThresholds.remove(chatId);
        }
    }

    public static void clearAll(Long chatId) {
        userThresholds.remove(chatId);
    }

    public static boolean hasNotifications(Long chatId) {
        Map<String, Threshold> map = userThresholds.get(chatId);
        return map != null && !map.isEmpty();
    }

    public static void updateUserThresholds(Long chatId) {

    }

    private static void pollAndNotify() {
        try {
            if (userThresholds.isEmpty()) return;

            Map<String, Double> rates = FreeCurrencyAPI.getRubRates();

            List<NotificationToSend> toSend = new ArrayList<>(); // Избегаем изменения коллекции при итерировании
            //проверка каждого порога и добавление их в отправку, если порог был преодолен
            for (Map.Entry<Long, ConcurrentMap<String, Threshold>> entry : userThresholds.entrySet()) {
                Long chatId = entry.getKey();
                Map<String, Threshold> thresholds = entry.getValue();

                for (Map.Entry<String, Threshold> e : thresholds.entrySet()) {
                    String currency = e.getKey();
                    Threshold threshold = e.getValue();
                    Double rate = rates.get(currency);
                    if (rate == null) continue;

                    boolean matches = threshold.matches(rate);
                    if (matches) {
                        toSend.add(new NotificationToSend(chatId, currency, rate, threshold));
                    }
                }
            }

            for (NotificationToSend n : toSend) {
                String text = String.format("Уведомление: %s достиг %.2f RUB (условие: %s %s)",
                        n.currency,
                        n.rate,
                        n.threshold.type == Threshold.Type.GREATER_EQUAL ? ">=" : "<=",
                        n.threshold.value);
                sendTelegramMessage(n.chatId, text);

                // удаляем порог — одноразовое уведомление
                removeThreshold(n.chatId, n.currency);
            }

        } catch (Exception ex) {
            // простое логирование — замени на логгер при желании
            ex.printStackTrace();
        }
    }

    private static class NotificationToSend {
        final Long chatId;
        final String currency;
        final double rate;
        final Threshold threshold;

        NotificationToSend(Long chatId, String currency, double rate, Threshold threshold) {
            this.chatId = chatId;
            this.currency = currency;
            this.rate = rate;
            this.threshold = threshold;
        }
    }

    private static void sendTelegramMessage(Long chatId, String text) {
        if (botInstance == null) {
            System.err.println("NotService: botInstance == null, не могу отправить сообщение: " + text);
            return;
        }
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId.toString());
        msg.setText(text);
        try {
            botInstance.execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

