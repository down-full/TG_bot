package org.example.bot.ComTests;

import org.example.bot.Commands.AboutCommand;
import org.example.bot.Commands.AuthorsCommand;
import org.example.bot.Commands.Command;
import org.example.bot.Commands.HelpCommand;
import org.example.bot.Commands.StartCommand;
import org.example.bot.Core.Executer;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class HelpCommandTest {

    private HelpCommand helpCommand;
    private Long chatId;
    private String messageText;
    private Map<String, Command> commands;

    @Before
    public void setUp() {
        commands = new LinkedHashMap<>();
        new StartCommand().register(commands);
        new AboutCommand().register(commands);
        new AuthorsCommand().register(commands);
        helpCommand = new HelpCommand(commands);
        chatId = 12345L;
        messageText = "/help";
    }

    @Test
    public void testGetName() {
        assertEquals("/help", helpCommand.getName());
    }

    @Test
    public void testGetDescription() {
        assertEquals("Расскажет о доступных командах", helpCommand.getDescription());
    }

    @Test
    public void testExecute() {
        Executer executer = new Executer() {
            @Override
            public void sendMessage(Long chatId, String messageText) {
                assertTrue(messageText.contains("Список доступных команд\n\n"));
                assertTrue(messageText.contains("/start - Начало работы с ботом"));
                assertTrue(messageText.contains("/about - Информация о боте"));
                assertTrue(messageText.contains("/authors - Информация об авторах"));
                assertTrue(messageText.contains("/help - Расскажет о доступных командах"));
            }
        };
        helpCommand.execute(chatId, messageText);
    }
}
