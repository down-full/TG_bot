package org.example.bot;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class StartCommandTest {
    private StartCommand startCommand;
    private Long chatId;
    private String messageText;
    private String lastSentMessage;

    @Before
    public void setUp() {
        startCommand = new StartCommand();

        startCommand = new StartCommand() {
            @Override
            public void sendMessageWithKeyboard(Long chatId, String messageText) {
                lastSentMessage = messageText;
            }
        };

        chatId = 12345L;
        messageText = "/start";
        lastSentMessage = null;
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
    public void execute_firstTime() {
        
        startCommand.execute(chatId, messageText);
        assertEquals("Привет! Это бот-конвертатор валют.\n\nИспользуйте кнопки ниже:", lastSentMessage);
    }
}