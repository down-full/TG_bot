package org.example.bot;

import org.example.bot.BotStateService.BotState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.DecimalFormat;
import java.util.Map;

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
                "1) Введите /convert\n" +
                "2) Отправьте сообщение в формате: <сумма><из валюты><в валюту>\n" +
                "Пример: 100 usd eur";
    }

    @Override
    public void execute(Long chatId, String messageText) {
        BotStateService.BotState state = BotStateService.getState(chatId);

        if (state == BotState.AWAITING_ARGS) {
            Conversion(chatId, messageText);
            BotStateService.clearState(chatId);
        } else {
            BotStateService.setState(chatId, BotState.AWAITING_ARGS);
            sendMessage(chatId, "Введите данные для конвертации в формате:\n" + 
            "<сумма> <из валюты> <в валюту>\n" +
            "Пример: 100 usd eur");
        }
    }

    public void Conversion(Long chatId, String args) {
        try {
            String[] arguments = args.trim().split("\\s+");

            if (arguments.length < 3) {
                sendMessage(chatId, "Неверный формат ввода аргументов!");
                return;
            }

            double amount = parseAmount(arguments[0]);

            if (amount <= 0) {
                sendMessage(chatId, "Сумма должна быть положительной!");
                return;
            }

            String fromCurrency = FreeCurrencyAPI.normalizeCurrencyCode(arguments[1]);
            String toCurrency = FreeCurrencyAPI.normalizeCurrencyCode(arguments[2]);

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
                    "Результат конвертации:\n%s %s = %s %s\nКурс: 1 %s = %.4f %s",
                    DF.format(amount), fromCurrency,
                    DF.format(result), toCurrency,
                    fromCurrency, (fromRate / toRate), toCurrency
            );

            sendMessage(chatId, response);

        } catch (NumberFormatException e){
            sendMessage(chatId, "Неверный формат суммы!");
        } catch (Exception e) {
            log.error("Ошибка при конвертации, {}", args, e);
            sendMessage(chatId, "Ошибка при конвертации. Попробуйте позже.");
        }
    }
    private double parseAmount(String amountStr) throws NumberFormatException { 
        return Double.parseDouble(amountStr.replace(",", "."));
    }
}