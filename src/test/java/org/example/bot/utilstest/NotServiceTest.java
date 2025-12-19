package org.example.bot.utilstest;

import org.example.bot.utils.NotService;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.*;

public class NotServiceTest {

    private static final long CHAT_ID = 12345L;

    @Before
    public void setUp() {
        NotService.clearAll(CHAT_ID);
        File f = new File("thresholds.txt");
        if (f.exists()) f.delete();
    }

    @Test // На корректность добавления порогового значения для конкретной валюты
         // что после удаления у пользователя больше нет активных уведомлений
    public void testSetThreshold() {
        NotService.setThreshold(CHAT_ID, "USD",
                new NotService.Threshold(NotService.Threshold.Type.GREATER_EQUAL, 90.5));
        Map<String, NotService.Threshold> map = NotService.getUserThresholds(CHAT_ID);
        assertTrue(map.containsKey("USD"));
        assertEquals(90.5, map.get("USD").value, 0.0001);
        assertEquals(NotService.Threshold.Type.GREATER_EQUAL, map.get("USD").type);
    }

    @Test //На корректность удаления установленного порога и проверяет, 
         // что после удаления у пользователя больше нет активных уведомлений
    public void testRemoveThreshold() {
        NotService.setThreshold(CHAT_ID, "EUR",
                new NotService.Threshold(NotService.Threshold.Type.LESS_EQUAL, 80.0));
        NotService.removeThreshold(CHAT_ID, "EUR");
        assertFalse(NotService.hasNotifications(CHAT_ID));
    }

    @Test// Проверяет, что метод clearAll полностью удаляет все установленные пороги
        // и оставляет пользователя без активных уведомлений
    public void testClearAll() {
        NotService.setThreshold(CHAT_ID, "USD",
                new NotService.Threshold(NotService.Threshold.Type.GREATER_EQUAL, 90));
        NotService.setThreshold(CHAT_ID, "EUR",
                new NotService.Threshold(NotService.Threshold.Type.LESS_EQUAL, 80));
        NotService.clearAll(CHAT_ID);
        assertFalse(NotService.hasNotifications(CHAT_ID));
        assertTrue(NotService.getUserThresholds(CHAT_ID).isEmpty());
    }

    @Test // Два теста на логику сравнения
    public void testThresholdMatchesGreaterEqual() {
        NotService.Threshold t =
                new NotService.Threshold(NotService.Threshold.Type.GREATER_EQUAL, 100);
        assertTrue(t.matches(100));
        assertTrue(t.matches(150));
        assertFalse(t.matches(99.9));
    }

    @Test
    public void testThresholdMatchesLessEqual() {
        NotService.Threshold t =
                new NotService.Threshold(NotService.Threshold.Type.LESS_EQUAL, 80);
        assertTrue(t.matches(79.9));
        assertTrue(t.matches(80));
        assertFalse(t.matches(81));
    }

    @Test// Проверяет, что установленные пороговые значения сохраняются
        // и могут быть корректно загружены после установки
    public void testSaveAndLoad() {
        NotService.setThreshold(CHAT_ID, "USD",
                new NotService.Threshold(NotService.Threshold.Type.GREATER_EQUAL, 90));
        Map<String, NotService.Threshold> map = NotService.getUserThresholds(CHAT_ID);
        assertTrue(map.containsKey("USD"));
        assertEquals(90, map.get("USD").value, 0.0001);
    }

    @Test // Тест лимита уведомлений
    public void testMaxNotificationsLimit() {
        NotService.clearAll(CHAT_ID);
        for (int i = 0; i < 10; i++) {
            NotService.setThreshold(CHAT_ID, "CUR" + i,
                    new NotService.Threshold(NotService.Threshold.Type.GREATER_EQUAL, i));
        }
        Map<String, NotService.Threshold> map = NotService.getUserThresholds(CHAT_ID);
        assertEquals("Должно быть ровно 10 элементов", 10, map.size());
        NotService.setThreshold(CHAT_ID, "EXTRA",
                new NotService.Threshold(NotService.Threshold.Type.GREATER_EQUAL, 999));
        map = NotService.getUserThresholds(CHAT_ID);
        assertFalse("EXTRA не должна быть добавлена при превышении лимита", 
                    map.containsKey("EXTRA"));

        assertEquals("Должно остаться ровно 10 элементов", 10, map.size());
    }
}

