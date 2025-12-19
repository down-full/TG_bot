package org.example.bot.comtests;

import org.example.bot.commands.RateCommand;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class RateCommandTest {

    private static class TestRateCommand extends RateCommand {
        private Long lastChatId;
        private String lastMessage;

        @Override
        public void sendMessage(Long chatId, String textMessage) {
            this.lastChatId = chatId;
            this.lastMessage = textMessage;
        }
    }

    private TestRateCommand command;

    @Before
    public void setUp() {
       command = new TestRateCommand();
    }

    public static class FreeCurrencyAPI {
        public static Map<String, Double> getRubRates() {
            Map<String, Double> map = new HashMap<>();
            map.put("USD", 100.0);
            map.put("EUR", 110.3);
            map.put("GBP", 108.25);
            return map;
        }

        public static String normalizeCurrencyCode(String code) {
            return code.toUpperCase();
        }
    }

    @Test
    public void testGetName() {
        assertEquals("/rate", command.getName());
    }

    @Test
    public void testGetDescription() {
        String description = command.getDescription();
        assertTrue(description.contains("/rate"));
        assertTrue(description.contains("валют"));
    }

    @Test
    public void testExecute_AllRates() {
        command.execute(1L, "/rate");
        assertNotNull(command.lastMessage);
        assertTrue(command.lastMessage.contains("USD"));
        assertTrue(command.lastMessage.contains("RUB"));
    }

    @Test
    public void testExecute_SingleRate() {
        command.execute(1L, "/rate usd");
        assertTrue(command.lastMessage.contains("USD"));
        assertTrue(command.lastMessage.contains("RUB"));
    }

    @Test
    public void testUnknownRate() {
        command.execute(1L, "/rate abc");
        assertTrue(command.lastMessage.contains("Валюта не найдена"));
    }
    
    @Test
    public void testExecute_InvalidFormat() {
        command.execute(1L, "/rate usd extra");
        assertTrue(command.lastMessage.contains("Неправильный формат"));
    }
}



