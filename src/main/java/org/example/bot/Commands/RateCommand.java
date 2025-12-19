package org.example.bot.commands;

import java.text.DecimalFormat;
import java.util.Map;
import org.example.bot.api.FreeCurrencyApi;
import org.example.bot.core.Executer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RateCommand extends Executer implements Command {
    private static final Logger log = LoggerFactory.getLogger(RateCommand.class);
    private static final DecimalFormat DF = new DecimalFormat("#,##0.00");

    @Override
    public String getName() {
        return "/rate";
    }

    @Override
    public String getDescription() {
        return "Курсы валют к RUB\n"
             + "Формат:\n" 
             + "/rate - все курсы\n"
             + "/rate <валюта> - курс конкретной валюты";
    }

    @Override
    public void execute(Long chatId, String messageText) {
        try {
            String[] args = messageText.split(" ");

            if (args.length == 1) {
                // Вывод всех курсов: /rate
                showAllRates(chatId);
            } else if (args.length == 2) {
                // Вывод курса одной валюты: /rate eur
                showSingleRate(chatId, args[1]);
            } else {
                sendMessage(chatId, "Неправильный формат. Используйте: " + getDescription());
            }
        } catch (Exception e) {
            log.error("Ошибка в /rate", e);
            sendMessage(chatId, "Ошибка при получении курсов. Попробуйте позже.");
        }
    }

    private void showAllRates(Long chatId) throws Exception {
        Map<String, Double> rubRates = FreeCurrencyApi.getRubRates();
        StringBuilder response = new StringBuilder("Курсы валют к RUB:\n\n");

        rubRates.forEach((currency, rate) ->
                response.append("1 ").append(currency)
                        .append(" = ")
                        .append(DF.format(rate))
                        .append(" RUB\n")
        );

        sendMessage(chatId, response.toString());
    }

    private void showSingleRate(Long chatId, String currency) throws Exception {
        currency = FreeCurrencyApi.normalizeCurrencyCode(currency);
        Map<String, Double> rubRates = FreeCurrencyApi.getRubRates();

        if (!rubRates.containsKey(currency)) {
            sendMessage(chatId, "Валюта не найдена. Доступные: " 
            + String.join(", ", rubRates.keySet()));
            return;
        }

        String response = "1 " + currency + " = " + DF.format(rubRates.get(currency)) + " RUB";
        sendMessage(chatId, response);
    }
}