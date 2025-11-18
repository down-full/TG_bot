package org.example.bot;

import java.util.Map;

public class NotifyCommand extends Executer implements Command {

    @Override
    public String getName() {
        return "/notify";
    }

    @Override
    public String getDescription() {
        return """
                Уведомления по валютам. Примеры:
                /notify set USD >= 90 - добавить порог
                /notify set EUR <= 80
                /notify clear USD - удалить порог (пример для USD)
                /notify clear all - удалить все пороги
                /notify status - показать текущие пороги
                /notify start - запустить сервис опроса 
                /notify stop - остановка всех уведомлений
                """;
    }

    @Override
    public void execute(Long chatId, String messageText) {
        try {
            String[] parts = messageText.trim().split("\\s+");
            if (parts.length == 1) {
                sendMessage(chatId, getDescription());
                return;
            }

            String action = parts[1].toLowerCase();
            switch (action) {
                case "start":
                    NotService.start();
                    sendMessage(chatId, "Сервис уведомлений запущен.");
                    break;
                case "stop":
                    NotService.clearAll(chatId);
                    sendMessage(chatId, "Все ваши уведомления удалены.");
                    break;
                case "status":
                    printStatus(chatId);
                    break;
                case "set":
                    handleSet(chatId, parts);
                    break;
                case "clear":
                    handleClear(chatId, parts);
                    break;
                default:
                    sendMessage(chatId, "Неизвестное действие. Используйте /notify.");
            }
        } catch (Exception e) {
            sendMessage(chatId, "Ошибка обработки команды /notify. Проверьте формат.");
        }
    }

    private void printStatus(Long chatId) {
        Map<String, NotService.Threshold> map = NotService.getUserThresholds(chatId);
        if (map.isEmpty()) {
            sendMessage(chatId, "У вас нет настроенных уведомлений.");
            return;
        }
        StringBuilder sb = new StringBuilder("Ваши уведомления:\n");
        map.forEach((cur, th) -> sb.append(cur).append(" ").append(th.toString()).append("\n")); //создаем строку для уведа
        sendMessage(chatId, sb.toString());
    }

    private void handleSet(Long chatId, String[] parts) {
        if (parts.length < 5) {
            sendMessage(chatId, "Неверный формат. Пример: /notify set USD >= 90");
            return;
        }
        String currency = FreeCurrencyAPI.normalizeCurrencyCode(parts[2]);
        String operation = parts[3];
        String valStr = parts[4];

        try {
            Map<String, Double> rates = FreeCurrencyAPI.getRubRates();
            if (!rates.containsKey(currency)) {
                sendMessage(chatId, "Валюта не найдена: " + currency);
                return;
            }
        } catch (Exception ex) {
            sendMessage(chatId, "Не могу проверить валюту сейчас — попробуйте позже.");
            return;
        }

        try {
            double val = Double.parseDouble(valStr);
            NotService.Threshold.Type type;
            if (operation.equals(">=") || operation.equals("=>")) type = NotService.Threshold.Type.GREATER_EQUAL;
            else if (operation.equals("<=") || operation.equals("=<")) type = NotService.Threshold.Type.LESS_EQUAL;
            else {
                sendMessage(chatId, "Неподдерживаемый оператор. Используйте >= или <=.");
                return;
            }
            NotService.setThreshold(chatId, currency, new NotService.Threshold(type, val));
            sendMessage(chatId, "Порог установлен: " + currency + " " + operation + " " + val);
        } catch (NumberFormatException ex) {
            sendMessage(chatId, "Неверное числовое значение порога.");
        }
    }

    private void handleClear(Long chatId, String[] parts) {
        if (parts.length < 3) {
            sendMessage(chatId, "Неверный формат. Пример: /notify clear USD или /notify clear all");
            return;
        }
        if (parts[2].equalsIgnoreCase("all")) {
            NotService.clearAll(chatId);
            sendMessage(chatId, "Все ваши пороги удалены.");
        } else {
            String currency = FreeCurrencyAPI.normalizeCurrencyCode(parts[2]);
            NotService.removeThreshold(chatId, currency);
            sendMessage(chatId, "Порог для " + currency + " удалён.");
        }
    }
}
