package org.example.bot;


import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;


public class TelegramBot extends TelegramLongPollingBot {


    @Override
    public void onUpdateReceived(Update update) {
        Handlers messageList = new Handlers();

        if (update.hasMessage() && update.getMessage().hasText()){
            System.out.println(update.getMessage().getText());
            Long chatID = update.getMessage().getChatId();
            String text = update.getMessage().getText().toLowerCase();
            messageList.telegramHandlers(chatID,text);
        }
    }

    @Override
    public String getBotToken() {
        return System.getenv("tgBotKey");
    }


    @Override
    public String getBotUsername() {
        return System.getenv("tgBotName");
    }

}
