package org.example;

import org.example.bot.TelegramBot;
import org.example.bot.NotService;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

            // создаём экземпляр бота и регистрируем
            TelegramBot bot = new TelegramBot();
            telegramBotsApi.registerBot(bot);

            // передаём экземпляр бота в сервис уведомлений и запускаем сервис
            NotService.setBotInstance(bot);
            NotService.start();

            // Добавим shutdown hook чтобы корректно завершать NotService
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down NotService...");
                NotService.shutdown();
            }));

            System.out.println("Бот запущен.");

        } catch (TelegramApiException e) {
            // логируем ошибку регистрации бота
            e.printStackTrace();
            System.err.println("Не удалось зарегистрировать бота: " + e.getMessage());
        }
    }
}

