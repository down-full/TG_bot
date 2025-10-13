package org.example.bot;

public class StartCommand extends Executer implements Command{
    private String answer = "Привет, это бот-конвертатор";
    private String started = "Бот уже запущен и готов к работе!";
    private boolean isStarted = false;

    @Override
    public String getName() {
        return "/start";
    }

    @Override
    public String getDescription() {
        return "Начало работы с ботом";
    }


    public void execute(Long chatId, String messageText) {
        System.out.println("isStarted = " + isStarted);
        if(!isStarted) {
            sendMessage(chatId, answer);
            isStarted = true;
        } else {
            sendMessage(chatId, started);
        }
    }
}
