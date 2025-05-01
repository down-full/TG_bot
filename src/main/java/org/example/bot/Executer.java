package org.example.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public abstract class Executer extends TelegramBot
{
    public void sendMessage(Long chatID, String messageText)
    {
        SendMessage message = new SendMessage();
        message.setChatId(chatID);
        message.setText(messageText);
        try{
            execute(message);
        }catch (TelegramApiException e){
            System.out.println("Uncorrected "+ e);
        }
    }
}
