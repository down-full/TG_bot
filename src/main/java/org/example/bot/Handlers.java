package org.example.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Handlers extends TelegramBot{
    LinkedHashMap<String, Consumer<Long>> command = new LinkedHashMap<>();
    LinkedHashMap<String, String> helpText = new LinkedHashMap<>();
    public String answer = "";


    public Handlers(){
    command.put("/start", this::start_command);
    command.put("/authors", this::authors_command);
    command.put("/about", this::about_command);



    helpText.put("start", "Эта команда начинает общение с ботом");
    helpText.put("authors", "Эта команда показывает авторов бота");
    helpText.put("about", "Эта команда расскажет о том, что умеет бот");
    helpText.put("/help", "Бот имеет следующие команды: \n/start\n/about\n/authors\n/help\nДля" +
                " подробной информации введите \"/help команда\"");
    helpText.put("help", "Вводя эту команду вы можете узнать какие команды умеет делать бот");
    }




    private void start_command(Long chatId) {
        answer = "Привет, это бот конвертатор";
        sendMessage(chatId, answer);
    }

    private void authors_command(Long chatId) {
        answer = "Этот бот делали Волокитина Валерия и Кудисов Арсений";
        sendMessage(chatId, answer);
    }

    private void about_command(Long chatId) {
        answer = "Конвертируем валюты и металлы";
        sendMessage(chatId, answer);
    }

    private void help_command(Long chatId) {
        StringBuilder message_text = new StringBuilder("Доступные команды:\n\n");
        for(Map.Entry<String, String> entry : helpText.entrySet()){
            String command_name = entry.getKey();
            String description = entry.getValue();

            message_text.append(command_name).append(" ").append(description).append("\n");
        }
        sendMessage(chatId, message_text.toString());

    }

    public void sendMessage(Long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.out.println("Incorrect: " + e);
        }
    }

    public void telegramHandlers(Long chatId, String messageText){
        if (messageText.contains("/help")) {
            if (messageText.length() > 6) {
                messageText = messageText.substring(6);
            }
            answer = helpText.get(messageText);
            sendMessage(chatId,answer);
        } else {
            if (command.containsKey(messageText)) {
                command.get(messageText).accept(chatId);
            } else {
                answer = "Нет такой команды\n";
                sendMessage(chatId, answer);
            }
        }
    }
}
