package org.example;

import org.example.bot.Core.TelegramBot;
import org.example.bot.Utils.NotService;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

            TelegramBot bot = new TelegramBot();
            telegramBotsApi.registerBot(bot);

            NotService.setBotInstance(bot);
            NotService.start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Остановка сервиса уведомлений...");
                NotService.shutdown();
            }));

            System.out.println("Бот запущен.");

        } catch (TelegramApiException e) {
            e.printStackTrace();
            System.err.println("Не удалось зарегистрировать бота: " + e.getMessage());
        }
    }
}

