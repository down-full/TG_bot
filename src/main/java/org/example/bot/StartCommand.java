package org.example.bot;

public class StartCommand extends Executer implements Command{
    private String answer = "Привет, это бот-конвертатор";
    @Override
    public String getName() {
        return "/start";
    }

    @Override
    public String getDescription() {
        return "Начало работы с ботом";
    }


    public void execute(Long chatId, String messageText) {

        sendMessage(chatId, answer);
    }
}
