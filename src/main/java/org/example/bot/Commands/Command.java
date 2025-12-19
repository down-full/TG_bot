package org.example.bot.commands;

import java.util.Map;

public interface Command {

    String getName();

    String getDescription();

    void execute(Long chatId, String messageText);
    
    default void register(Map<String, Command> registry) {
        registry.put(getName(), this);
    }
}