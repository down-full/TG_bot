package org.example.bot;

public class StartCommand extends Executer implements Command {

    @Override
    public String getName() {
        return "/start";
    }

    @Override
    public String getDescription() {
        return "Начало работы с ботом";
    }

    @Override
    public void execute(Long chatId, String messageText) {
        sendMessageWithKeyboard(chatId, "Привет! Это бот-конвертатор валют.\n\nИспользуйте кнопки ниже:");
    }
}