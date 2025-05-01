package org.example.bot;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AuthorsCommandTest {

    private AuthorsCommand authorsCommand;
    private Long chatId;
    private String messageText;

    @Before
    public void setUp() {
        authorsCommand = new AuthorsCommand();
        chatId = 12345L;
        messageText = "/authors";
    }

    @Test
    public void testGetName() {
        assertEquals("/authors", authorsCommand.getName());
    }

    @Test
    public void testGetDescription() {
        assertEquals("Авторы бота", authorsCommand.getDescription());
    }

    @Test
    public void testExecute() {
        Executer executer = new Executer() {
            @Override
            public void sendMessage(Long chatId, String messageText) {
                assertEquals("Авторы: Волокитина Валерия и Кудисов Арсений", messageText);
            }
        };
        authorsCommand.execute(chatId, messageText);
    }
}
