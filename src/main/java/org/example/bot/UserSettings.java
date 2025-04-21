package org.example.bot;

import java.util.concurrent.ConcurrentHashMap;

public class UserSettings {
    private ConcurrentHashMap<Long, UserSubscription> userSubscriptions;

    public UserSettings() {
        this.userSubscriptions = new ConcurrentHashMap<>();
    }

    /**
     * Подписывает пользователя на уведомления.
     *
     * @param chatId    Идентификатор чата пользователя.
     * @param currency  Валюта, за которой следит пользователь.
     * @param threshold Порог изменения курса валюты, при котором отправляется уведомление.
     */
    public void subscribe(long chatId, String currency, double threshold) {
        userSubscriptions.put(chatId, new UserSubscription(currency, threshold));
    }

    /**
     * Возвращает настройки подписки пользователя.
     *
     * @param chatId Идентификатор чата пользователя.
     * @return Настройки подписки пользователя.
     */
    public UserSubscription getSubscription(long chatId) {
        return userSubscriptions.get(chatId);
    }

    /**
     * Удаляет подписку пользователя.
     *
     * @param chatId Идентификатор чата пользователя.
     */
    public void unsubscribe(long chatId) {
        userSubscriptions.remove(chatId);
    }

    private static class UserSubscription {
        private String currency;
        private double threshold;
        private double lastRate;

        public UserSubscription(String currency, double threshold) {
            this.currency = currency;
            this.threshold = threshold;
            this.lastRate = 0; // Инициализируем последний курс нулем или текущим курсом
        }

        public String getCurrency() {
            return currency;
        }

        public double getThreshold() {
            return threshold;
        }

        public double getLastRate() {
            return lastRate;
        }

        public void setLastRate(double lastRate) {
            this.lastRate = lastRate;
        }
    }
}
