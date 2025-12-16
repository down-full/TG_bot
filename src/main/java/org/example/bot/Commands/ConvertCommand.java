package org.example.bot.Commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.DecimalFormat;
import java.util.Map;

import org.example.bot.API.FreeCurrencyAPI;
import org.example.bot.Core.Executer;

public class ConvertCommand extends Executer implements Command {
    private static final Logger log = LoggerFactory.getLogger(ConvertCommand.class);
    private static final DecimalFormat DF = new DecimalFormat("#,##0.##");

    @Override
    public String getName() {
        return "/convert";
    }

    @Override
    public String getDescription() {
        return "Конвертация валют\n" +
                "Формат: /convert <сумма> <из валюты> <в валюту>\n";
    }

    @Override
    public void execute(Long chatId, String messageText) {
        try {
            String[] args = messageText.trim().split("\\s+");
            if (args.length != 4) {
                sendMessage(chatId, "Неверный формат. Используйте: " + getDescription());
                return;
            }

            double amount = parseAmount(args[1]);

            if (amount <= 0) {
                sendMessage(chatId, "Сумма должна быть положительным числом");
                return;
            }

            String fromCurrency = FreeCurrencyAPI.normalizeCurrencyCode(args[2]);

            String toCurrency = FreeCurrencyAPI.normalizeCurrencyCode(args[3]);

            Map<String, Double> rubRates = FreeCurrencyAPI.getRubRates();

            if (!rubRates.containsKey(fromCurrency) || !rubRates.containsKey(toCurrency)) {
                sendMessage(chatId, "Одна из валют не найдена. Доступные: " +
                        String.join(", ", rubRates.keySet()));
                return;
            }

            double fromRate = rubRates.get(fromCurrency);
            double toRate = rubRates.get(toCurrency);
            double result = (amount * fromRate) / toRate;

            String response = String.format(
                    "Результат конвертации:\n" +
                            "%s %s = %s %s\n" +
                            "Курс: 1 %s = %.4f %s",


                    DF.format(amount), fromCurrency,
                    DF.format(result), toCurrency,
                    fromCurrency, (fromRate / toRate), toCurrency


            );
            sendMessage(chatId, response);

        } catch (NumberFormatException e) {
            sendMessage(chatId, "Неверный формат суммы. Пример: 100 или 50.25");

        } catch (Exception e) {
            log.error("Ошибка в /convert: {}", messageText, e);
            sendMessage(chatId, "Ошибка при конвертации. Попробуйте позже.");
        }


    }
    private double parseAmount(String amountStr) throws NumberFormatException {
        return Double.parseDouble(amountStr.replace(",", "."));
    }
}