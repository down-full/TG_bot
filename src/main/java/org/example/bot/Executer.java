package org.example.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.ArrayList;
import java.util.List;

public abstract class Executer extends TelegramBot {

    public void sendMessage(Long chatID, String messageText) {
        SendMessage message = new SendMessage();
        message.setChatId(chatID.toString());
        message.setText(messageText);
        try{
            execute(message);
        } catch (TelegramApiException e){
            System.out.println("Uncorrected "+ e);
        }
    }

    // НОВЫЙ МЕТОД - отправка сообщения с кнопками
    public void sendMessageWithKeyboard(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setReplyMarkup(createMainKeyboard());

        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Ошибка отправки: " + e.getMessage());
        }
    }

    private ReplyKeyboardMarkup createMainKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("/start"));
        row1.add(new KeyboardButton("/help"));
        row1.add(new KeyboardButton("/authors"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("/rate"));
        row2.add(new KeyboardButton("/fadd"));
        row2.add(new KeyboardButton("/about"));

        keyboard.add(row1);
        keyboard.add(row2);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }
}