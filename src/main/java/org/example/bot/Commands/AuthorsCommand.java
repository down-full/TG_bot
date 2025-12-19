package org.example.bot.commands;

import org.example.bot.core.Executer;

public class AuthorsCommand extends Executer implements Command {
    private String answer = "Авторы: Волокитина Валерия и Кудисов Арсений";

    @Override
    public String getName() {
        return "/authors";
    }

    @Override
    public String getDescription() {
        return "Авторы бота";
    }

    public void execute(Long chatId, String messageText) {
        sendMessage(chatId, answer);
    }
}
