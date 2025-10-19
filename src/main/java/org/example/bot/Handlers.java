package org.example.bot;

import java.util.LinkedHashMap;
import java.util.Map;

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
    }

    public void telegramHandlers(Long chatId, String messageText) {
        if (messageText == null || messageText.isBlank()) {
            sendMessage(chatId, "Введите команду");
            return;
        }

        BotStateService.BotState state = BotStateService.getState(chatId);
        if (state == BotStateService.BotState.AWAITING_ARGS) {
            Command convertCommand = commands.get("/convert");
            if (convertCommand != null) {
                ((ConvertCommand) convertCommand).Conversion(chatId, messageText);
                BotStateService.clearState(chatId);
            }
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
