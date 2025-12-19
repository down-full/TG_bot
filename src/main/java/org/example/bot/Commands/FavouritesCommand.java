package org.example.bot.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.example.bot.api.FreeCurrencyApi;
import org.example.bot.core.Executer;
import org.example.bot.utils.NotService;

public class FavouritesCommand extends Executer implements Command {
    private static final Map<Long, List<String>> userFavorites = new HashMap<>();
    private static final int MAX_FAVOURITES = 10;

    @Override
    public String getName() {
        return "/fadd";
    }

    @Override
    public String getDescription() {
        return """
                Управление избранными валютами. Форматы:
                /fadd - показать избранные валюты и их курсы
                /fadd USD EUR - добавить валюты в избранное
                /fadd clear - очистить все избранное
                /fadd clear USD EUR - удалить конкретные валюты""";
    }

    @Override
    public void execute(Long chatId, String messageText) {
        try {
            String[] args = messageText.split(" ");

            if (args.length == 1) {
                showFavRates(chatId);
            } else if (args[1].equalsIgnoreCase("clear")) {
                if (args.length == 2) {
                    clearAllFav(chatId);
                } else {
                    clearFav(chatId, args);
                }
            } else {
                addFav(chatId, args);
            }
        } catch (IllegalArgumentException e) {
            sendMessage(chatId, "Ошибка: неправильный код валюты");
        } catch (IndexOutOfBoundsException e) {
            sendMessage(chatId, "Ошибка формата команды");
        } catch (Exception e) {
            sendMessage(chatId, "Внутренняя ошибка бота. Попробуйте еще раз.");
        }
    }

    private void addFav(Long chatId, String[] args) {
        List<String> favourites = userFavorites.getOrDefault(chatId, new ArrayList<>());

        try {
            Map<String, Double> availableRates = FreeCurrencyApi.getRubRates();
            List<String> added = new ArrayList<>();
            List<String> invalid = new ArrayList<>();

            for (int i = 1; i < args.length && favourites.size() < MAX_FAVOURITES; ++i) {
                String currency = FreeCurrencyApi.normalizeCurrencyCode(args[i]);

                if (!favourites.contains(currency)) {
                    if (availableRates.containsKey(currency)) {
                        favourites.add(currency);
                        added.add(currency);
                    } else {
                        invalid.add(currency);
                    }
                }
            }

            userFavorites.put(chatId, favourites);

            StringBuilder response = new StringBuilder();
            if (!added.isEmpty()) {
                response.append("Добавлено: ").append(String.join(", ", added));
            }
            if (!invalid.isEmpty()) {
                if (!added.isEmpty()) { 
                    response.append("\n"); 
                }
                response.append("Неверные валюты: ").append(String.join(", ", invalid));
            }
            if (added.isEmpty() && invalid.isEmpty()) {
                response.append("Не указано валют для добавления");
            }

            if (favourites.size() == MAX_FAVOURITES) {
                if (!response.isEmpty()) { 
                    response.append("\n"); 
                }
                response.append("Достигнут лимит избранных валют (")
                .append(MAX_FAVOURITES)
                .append(")");
            }

            sendMessage(chatId, response.toString());

        } catch (Exception e) {
            sendMessage(chatId, "Ошибка при проверке валют. Попробуйте позже.");
        }
        NotService.updateUserThresholds(chatId);
    }

    private void clearFav(Long chatId, String[] args) {
        List<String> favourites = userFavorites.get(chatId);

        if (favourites == null || favourites.isEmpty()) {
            sendMessage(chatId, "У вас нет избранных валют");
            return;
        }

        List<String> removed = new ArrayList<>();
        List<String> notFound = new ArrayList<>();

        for (int i = 2; i < args.length; ++i) {
            String currency = FreeCurrencyApi.normalizeCurrencyCode(args[i]);
            if (favourites.contains(currency)) {
                favourites.remove(currency);
                removed.add(currency);
            } else {
                notFound.add(currency);
            }
        }

        if (favourites.isEmpty()) {
            userFavorites.remove(chatId);
        } else {
            userFavorites.put(chatId, favourites);
        }

        StringBuilder response = new StringBuilder();
        if (!removed.isEmpty()) {
            response.append("Удалено: ").append(String.join(", ", removed));
        }
        if (!notFound.isEmpty()) {
            if (!removed.isEmpty()) { 
                response.append("\n"); 
            }
            response.append("Не найдено: ").append(String.join(", ", notFound));
        }
        if (removed.isEmpty() && notFound.isEmpty()) {
            response.append("Не указано валют для удаления");
        }

        sendMessage(chatId, response.toString());
        NotService.updateUserThresholds(chatId);
    }

    private void clearAllFav(Long chatId) {
        userFavorites.remove(chatId);
        sendMessage(chatId, "Все избранные валюты очищены");
    }

    private void showFavRates(Long chatId) {
        List<String> favourites = userFavorites.get(chatId);

        if (favourites == null || favourites.isEmpty()) {
            sendMessage(chatId, "У вас нет избранных валют. Добавьте через: /fadd USD EUR");
            return;
        }

        try {
            Map<String, Double> rubRates = FreeCurrencyApi.getRubRates();
            StringBuilder response = new StringBuilder("Курсы избранных валют:\n\n");

            for (String currency : favourites) {
                response.append("1 ").append(currency)
                        .append(" = ").append(String.format("%.2f", rubRates.get(currency)))
                        .append(" RUB\n");
            }

            sendMessage(chatId, response.toString());

        } catch (Exception e) {
            sendMessage(chatId, "Ошибка при получении курсов валют. Попробуйте позже.");
        }
        if (FavouritesCommand.getFavorites(chatId).isEmpty()) {
            NotService.clearAll(chatId);
        }
    }
    
    public static List<String> getFavorites(Long chatId) {
        return userFavorites.getOrDefault(chatId, new ArrayList<>());
    }
}