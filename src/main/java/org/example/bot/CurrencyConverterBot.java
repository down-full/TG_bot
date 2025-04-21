package org.example.bot;

import org.json.JSONObject;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class CurrencyConverterBot extends TelegramLongPollingBot {

    private final String BOT_USERNAME = "YOUR_BOT_USERNAME";
    private final String BOT_TOKEN = "YOUR_BOT_TOKEN";
    private CurrencyAPI currencyApi;
    private JSONObject rates;

    public CurrencyConverterBot() {
        this.currencyApi = new ExChAPI();
        this.rates = currencyApi.getExchangeRates();
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {


            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    handleStartCommand(chatId);
                    break;
                case "/help":
                    handleHelpCommand(chatId);
                    break;
                case "/authors":
                    handleAuthorsCommand(chatId);
                    break;
                default:
                    handleConversion(messageText, chatId);
                    break;
            }
        }
    } //словарь делегатов

    private void handleStartCommand(long chatId) {
        String response = "Добро пожаловать в Currency Converter Bot!\n"
                + "Используйте команды /help для получения информации о доступных функциях.";
        sendResponse(chatId, response);
    }

    private void handleHelpCommand(long chatId) {
        String response = "Доступные команды:\n"
                + "/start - начало работы с ботом\n"
                + "/help - вывод информации о командах\n"
                + "/authors - информация об авторах\n"
                + "Для конвертации валюты или драгоценных металлов используйте формат:\n"
                + "<из чего> <в что> <сумма> <currency/metal>\n"
                + "Пример: USD EUR 100 currency";
        sendResponse(chatId, response);
    }

    private void handleAuthorsCommand(long chatId) {
        String response = "Авторы этого бота:\n"
                + "Ваше Имя - разработчик\n"
                + "Другие Имя - соавтор";
        sendResponse(chatId, response);
    }



    private void handleConversion(String messageText, long chatId) {

        try {
            String[] parts = messageText.split(" ");
            if (parts.length == 4) {
                String fromCurrency = parts[0];
                String toCurrency = parts[1];
                double amount = Double.parseDouble(parts[2]);
                String type = parts[3]; // "currency" или "metal"

                if (type.equalsIgnoreCase("currency")) {
                    StandardCurrencyConverter converter = new StandardCurrencyConverter(rates);
                    double result = converter.convert(fromCurrency, toCurrency, amount);
                    sendResponse(chatId, "Результат: " + result);
                } else if (type.equalsIgnoreCase("metal")) {
                    PreciousMetalConverter metalConverter = new PreciousMetalConverter(rates);
                    double result = metalConverter.convert(fromCurrency, toCurrency, amount);
                    sendResponse(chatId, "Результат: " + result);
                } else {
                    sendResponse(chatId, "Неизвестный тип конвертации. Используйте 'currency' или 'metal'.");
                }
            } else {
                sendResponse(chatId, "Неверный формат команды. Используйте: <из чего> <в что> <сумма> <currency/metal>");
            }
        } catch (Exception e) {
            sendResponse(chatId, "Ошибка при конвертации: " + e.getMessage());
        }
    }

    private void sendResponse(long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
