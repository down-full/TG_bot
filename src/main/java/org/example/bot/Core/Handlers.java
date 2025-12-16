package org.example.bot.Core;

import java.util.LinkedHashMap;
import java.util.Map;

import org.example.bot.Commands.AboutCommand;
import org.example.bot.Commands.AuthorsCommand;
import org.example.bot.Commands.ChartCommand;
import org.example.bot.Commands.Command;
import org.example.bot.Commands.ConvertCommand;
import org.example.bot.Commands.FavouritesCommand;
import org.example.bot.Commands.HelpCommand;
import org.example.bot.Commands.NotifyCommand;
import org.example.bot.Commands.RateCommand;
import org.example.bot.Commands.StartCommand;

public class Handlers extends Executer {
    private final Map<String, Command> commands = new LinkedHashMap<>();

    public Handlers() {
        registerCommand();
    }

    private void registerCommand() {
        new StartCommand().register(commands);
        new AboutCommand().register(commands);
        new AuthorsCommand().register(commands);
        new HelpCommand(commands).register(commands);
        new ConvertCommand().register(commands);
        new RateCommand().register(commands);
        new FavouritesCommand().register(commands);
        new NotifyCommand().register(commands);
        new ChartCommand(this).register(commands);
    }

    public void telegramHandlers(Long chatId, String messageText) {
        if (messageText == null || messageText.isBlank()) {
            sendMessage(chatId, "Введите команду");
            return;
        }

        String commandName = messageText.split(" ")[0];
        Command command = commands.get(commandName);
        if (command != null) {
            command.execute(chatId, messageText);
        } else {
            sendMessage(chatId, "Неизвестная команда. Используйте /help для получения списка команд.");
        }
    }
}