package org.example.bot.ComTests;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import org.example.bot.Commands.AboutCommand;
import org.example.bot.Core.Executer;

public class AboutCommandTest {

    private AboutCommand aboutCommand;
    private Long chatId;
    private String messageText;

    @Before
    public void setUp() {
        aboutCommand = new AboutCommand();
        chatId = 12345L;
        messageText = "/about";
    }

    @Test
    public void testGetName() {
        assertEquals("/about", aboutCommand.getName());
    }

    @Test
    public void testGetDescription() {
        assertEquals("О возможностях бота", aboutCommand.getDescription());
    }

    @Test
    public void testExecute() {

        Executer executer = new Executer() {
            @Override
            public void sendMessage(Long chatId, String messageText) {
                assertEquals("Этот бот конвертирует валюты и металлы", messageText);
            }
        };
        aboutCommand.execute(chatId, messageText);
    }
}
