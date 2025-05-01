package org.example.bot;

public class AboutCommand extends Executer implements Command{
    private String answer = "Этот бот конвертирует валюты и металлы";
    @Override
    public String getName() {
        return "/about";
    }

    @Override
    public String getDescription() {
        return "О возможностях бота";
    }


    public void execute(Long chatId, String messageText) {
        sendMessage(chatId, answer);
    }
}
