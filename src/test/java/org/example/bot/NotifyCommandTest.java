package org.example.bot;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.Assert.*;

public class NotifyCommandTest {
    private NotifyCommand notifyCommand;
    private Long chatId;
    private String lastSentMessage;

    @Before
    public void setUp() {
        notifyCommand = new NotifyCommand() {
            @Override
            public void sendMessage(Long chatId, String messageText) {
                lastSentMessage = messageText;
            }
        };
        chatId = 12345L;
        lastSentMessage = null;
        NotService.clearAll(chatId);
    }

    @Test
    public void getName() {
        assertEquals("/notify", notifyCommand.getName());
    }

    @Test
    public void getDescription() {
        assertNotNull(notifyCommand.getDescription());
        assertTrue(notifyCommand.getDescription().contains("Уведомления по валютам"));
    }

    @Test
    public void printStatus_emptyThresholds() throws Exception {
        Method printStatusMethod = NotifyCommand.class.getDeclaredMethod("printStatus", Long.class);
        printStatusMethod.setAccessible(true);
        printStatusMethod.invoke(notifyCommand, chatId);
        assertEquals("У вас нет настроенных уведомлений.", lastSentMessage);
    }

    @Test
    public void printStatus_withThresholds() throws Exception {
        NotService.Threshold threshold1 = new NotService.Threshold(NotService.Threshold.Type.GREATER_EQUAL, 90.0);
        NotService.Threshold threshold2 = new NotService.Threshold(NotService.Threshold.Type.LESS_EQUAL, 80.0);
        NotService.setThreshold(chatId, "USD", threshold1);
        NotService.setThreshold(chatId, "EUR", threshold2);
        Method printStatusMethod = NotifyCommand.class.getDeclaredMethod("printStatus", Long.class);
        printStatusMethod.setAccessible(true);
        printStatusMethod.invoke(notifyCommand, chatId);
        assertNotNull(lastSentMessage);
        assertTrue(lastSentMessage.contains("Ваши уведомления:"));
        assertTrue(lastSentMessage.contains("USD"));
        assertTrue(lastSentMessage.contains("EUR"));
        assertTrue(lastSentMessage.contains("90.0"));
        assertTrue(lastSentMessage.contains("80.0"));
    }

    @Test
    public void handleSet_validGreaterEqual() throws Exception {
        String[] parts = {"", "set", "USD", ">=", "90.5"};
        Method handleSetMethod = NotifyCommand.class.getDeclaredMethod("handleSet", Long.class, String[].class);
        handleSetMethod.setAccessible(true);
        handleSetMethod.invoke(notifyCommand, chatId, parts);
        assertEquals("Порог установлен: USD >= 90.5", lastSentMessage);
        Map<String, NotService.Threshold> thresholds = NotService.getUserThresholds(chatId);
        assertTrue(thresholds.containsKey("USD"));
        assertNotNull(thresholds.get("USD"));
    }

    @Test
    public void handleSet_validLessEqual() throws Exception {
        String[] parts = {"", "set", "EUR", "<=", "85.0"};
        Method handleSetMethod = NotifyCommand.class.getDeclaredMethod("handleSet", Long.class, String[].class);
        handleSetMethod.setAccessible(true);
        handleSetMethod.invoke(notifyCommand, chatId, parts);
        assertEquals("Порог установлен: EUR <= 85.0", lastSentMessage);
        Map<String, NotService.Threshold> thresholds = NotService.getUserThresholds(chatId);
        assertTrue(thresholds.containsKey("EUR"));
        assertNotNull(thresholds.get("EUR"));
    }

    @Test
    public void handleSet_alternativeOperator() throws Exception {
        String[] parts = {"", "set", "USD", "=>", "95.0"};
        Method handleSetMethod = NotifyCommand.class.getDeclaredMethod("handleSet", Long.class, String[].class);
        handleSetMethod.setAccessible(true);
        handleSetMethod.invoke(notifyCommand, chatId, parts);
        assertEquals("Порог установлен: USD => 95.0", lastSentMessage);
        Map<String, NotService.Threshold> thresholds = NotService.getUserThresholds(chatId);
        assertTrue(thresholds.containsKey("USD"));
    }

    @Test
    public void handleSet_invalidFormat() throws Exception {
        String[] parts = {"", "set", "USD"}; 
        Method handleSetMethod = NotifyCommand.class.getDeclaredMethod("handleSet", Long.class, String[].class);
        handleSetMethod.setAccessible(true);
        handleSetMethod.invoke(notifyCommand, chatId, parts);
        assertEquals("Неверный формат. Пример: /notify set USD >= 90", lastSentMessage);
    }

    @Test
    public void handleSet_invalidOperator() throws Exception {
        String[] parts = {"", "set", "USD", ">", "90"}; 
        Method handleSetMethod = NotifyCommand.class.getDeclaredMethod("handleSet", Long.class, String[].class);
        handleSetMethod.setAccessible(true);
        handleSetMethod.invoke(notifyCommand, chatId, parts);
        assertEquals("Неподдерживаемый оператор. Используйте >= или <=.", lastSentMessage);
    }

    @Test
    public void handleSet_invalidNumber() throws Exception {
        String[] parts = {"", "set", "USD", ">=", "abc"}; // Нечисловое значение
        Method handleSetMethod = NotifyCommand.class.getDeclaredMethod("handleSet", Long.class, String[].class);
        handleSetMethod.setAccessible(true);
        handleSetMethod.invoke(notifyCommand, chatId, parts);
        assertEquals("Неверное числовое значение порога.", lastSentMessage);
    }

    @Test
    public void handleClear_singleCurrency() throws Exception {
        NotService.setThreshold(chatId, "USD", new NotService.Threshold(NotService.Threshold.Type.GREATER_EQUAL, 90.0));
        String[] parts = {"", "clear", "USD"};
        Method handleClearMethod = NotifyCommand.class.getDeclaredMethod("handleClear", Long.class, String[].class);
        handleClearMethod.setAccessible(true);
        handleClearMethod.invoke(notifyCommand, chatId, parts);  
        assertEquals("Порог для USD удалён.", lastSentMessage);
        Map<String, NotService.Threshold> thresholds = NotService.getUserThresholds(chatId);
        assertFalse(thresholds.containsKey("USD"));
    }

    @Test
    public void handleClear_allCurrencies() throws Exception {
        NotService.setThreshold(chatId, "USD", new NotService.Threshold(NotService.Threshold.Type.GREATER_EQUAL, 90.0));
        NotService.setThreshold(chatId, "EUR", new NotService.Threshold(NotService.Threshold.Type.LESS_EQUAL, 80.0));
        String[] parts = {"", "clear", "all"};
        Method handleClearMethod = NotifyCommand.class.getDeclaredMethod("handleClear", Long.class, String[].class);
        handleClearMethod.setAccessible(true);
        handleClearMethod.invoke(notifyCommand, chatId, parts);
        assertEquals("Все ваши пороги удалены.", lastSentMessage);
        Map<String, NotService.Threshold> thresholds = NotService.getUserThresholds(chatId);
        assertTrue(thresholds.isEmpty());
    }

    @Test
    public void handleClear_caseInsensitiveAll() throws Exception {
        String[] parts = {"", "clear", "ALL"}; // Все заглавными
        Method handleClearMethod = NotifyCommand.class.getDeclaredMethod("handleClear", Long.class, String[].class);
        handleClearMethod.setAccessible(true);
        handleClearMethod.invoke(notifyCommand, chatId, parts);
        assertEquals("Все ваши пороги удалены.", lastSentMessage);
    }

    @Test
    public void handleClear_invalidFormat() throws Exception {
        String[] parts = {"", "clear"}; 
        Method handleClearMethod = NotifyCommand.class.getDeclaredMethod("handleClear", Long.class, String[].class);
        handleClearMethod.setAccessible(true);
        handleClearMethod.invoke(notifyCommand, chatId, parts);
        assertEquals("Неверный формат. Пример: /notify clear USD или /notify clear all", lastSentMessage);
    }

    @Test
    public void handleClear_nonExistentCurrency() throws Exception {
        String[] parts = {"", "clear", "GBP"}; 
        Method handleClearMethod = NotifyCommand.class.getDeclaredMethod("handleClear", Long.class, String[].class);
        handleClearMethod.setAccessible(true);
        handleClearMethod.invoke(notifyCommand, chatId, parts);
        assertEquals("Порог для GBP удалён.", lastSentMessage);
    }

    @Test
    public void execute_setAction() {
        String messageText = "/notify set USD >= 90.5";
        notifyCommand.execute(chatId, messageText);
        assertTrue(lastSentMessage.contains("Порог установлен: USD >="));
        Map<String, NotService.Threshold> thresholds = NotService.getUserThresholds(chatId);
        assertTrue(thresholds.containsKey("USD"));
    }

    @Test
    public void execute_clearAction() {
        NotService.setThreshold(chatId, "USD", new NotService.Threshold(NotService.Threshold.Type.GREATER_EQUAL, 90.0));
        String messageText = "/notify clear USD";
        notifyCommand.execute(chatId, messageText);
        assertEquals("Порог для USD удалён.", lastSentMessage);
    }

    @Test
    public void execute_statusAction() {
        String messageText = "/notify status";
        notifyCommand.execute(chatId, messageText);
        assertEquals("У вас нет настроенных уведомлений.", lastSentMessage);
    }

    @Test
    public void execute_startAction() {
        String messageText = "/notify start";
        notifyCommand.execute(chatId, messageText);
        assertEquals("Сервис уведомлений запущен.", lastSentMessage);
    }

    @Test
    public void execute_stopAction() {
        String messageText = "/notify stop";
        notifyCommand.execute(chatId, messageText);
        assertEquals("Все ваши уведомления удалены.", lastSentMessage);
    }

    @Test
    public void execute_unknownAction() {
        String messageText = "/notify unknown";
        notifyCommand.execute(chatId, messageText);
        assertEquals("Неизвестное действие. Используйте /notify.", lastSentMessage);
    }

    @Test
    public void execute_emptyCommand() {
        String messageText = "/notify";
        notifyCommand.execute(chatId, messageText); 
        assertEquals(notifyCommand.getDescription(), lastSentMessage);
    }

    @Test
    public void execute_exceptionHandling() {
        String messageText = null;
        notifyCommand.execute(chatId, messageText);
        assertEquals("Ошибка обработки команды /notify. Проверьте формат.", lastSentMessage);
    }
}