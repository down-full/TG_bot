package org.example.bot;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ConvertCommandTest {
    
    private testConvertCommand command;

    public static class FreeCurrencyAPI {
        private static final Map<String, Double> rubRates = new HashMap<>();
        static {
            rubRates.put("USD", 90.0);
            rubRates.put("EUR", 100.0);
        }

        public static Map<String, Double> getRubRates() {
            return rubRates;
        }

        public static String normalizeCurrencyCode(String code) {
            return code.toUpperCase();
        }
    }

    private static class testConvertCommand extends ConvertCommand {
        String lastMessage;

        @Override
        public void sendMessage(Long chatId, String textMessage) {
            lastMessage = textMessage;
        }
    }

    @Before
    public void setUp() {
        command = new testConvertCommand();
    }

    @Test
    public void testGetName() {
        assertEquals("/convert", command.getName());
    }

    @Test
    public void testDescription() {
        assertTrue(command.getDescription().contains("Отправьте сообщение в формате:"));
    }

    @Test
    public void testInvalidFormat() {
        command.Conversion(1L, "100usd");
        assertEquals("Неверный формат ввода аргументов!", command.lastMessage);
    }

    @Test
    public void testNegativeFormat() {
        command.Conversion(1L, "-100 usd eur");
        assertEquals("Сумма должна быть положительной!", command.lastMessage); 
    }

    @Test
    public void testUnknownCurrency() {
        command.Conversion(1L, "100 sss eur");
        assertTrue(command.lastMessage.contains("Одна из валют не найдена"));
    }

    @Test
    public void testIsValid() {
        command.Conversion(1L, "100 usd eur");
        assertTrue(command.lastMessage.contains("Результат конвертации"));
        assertTrue(command.lastMessage.contains("USD"));
        assertTrue(command.lastMessage.contains("EUR"));
    }
    
    @Test
    public void testIsSumValid() {
        command.Conversion(1L, "abc usd eur");
        assertTrue(command.lastMessage.contains("Неверный формат суммы"));
    }
}
