package org.example.bot;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class StartCommandTest {
    private StartCommand startCommand;
    private Long chatId;
    private String messageText;

    @Before
    public void setUp() {
        startCommand = new StartCommand();
        chatId = 12345L;
        messageText = "/start";
    }

    @Test
    public void getName() {
    assertEquals("/start", startCommand.getName());
    }

    @Test
    public void getDescription() {
    assertEquals("Начало работы с ботом", startCommand.getDescription());
    }

    @Test
    public void execute() {
        Executer executer = new Executer() {
            @Override
            public void sendMessage(Long chatId, String messageText) {
                assertEquals("Привет, это бот-конвертатор", messageText);
            }
        };
        startCommand.execute(chatId, messageText);
    }
}