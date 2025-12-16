package org.example.bot.ComTests;

import org.example.bot.Commands.FavouritesCommand;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class FavouritesCommandTest {

    private FavouritesCommand command;
    private final Long CHAT_ID = 12345L;
    private Map<Long, String> sentMessages;

    @Before
    public void setUp() {
        command = new FavouritesCommand() {
            @Override
            public void sendMessage(Long chatId, String message) {
                sentMessages.put(chatId, message);
            }
        };
        sentMessages = new HashMap<>();
        FavouritesCommand.getFavorites(CHAT_ID).clear();
    }

    @Test
    public void testAddFavorites() {
        command.execute(CHAT_ID, "/fadd USD EUR");
        java.util.List<String> favorites = FavouritesCommand.getFavorites(CHAT_ID);
        assertEquals(2, favorites.size());
        assertTrue(favorites.contains("USD"));
        assertTrue(favorites.contains("EUR"));
    }

    @Test
    public void testShowFavoritesWhenEmpty() {
        command.execute(CHAT_ID, "/fadd");
        String response = sentMessages.get(CHAT_ID);
        assertTrue(response.contains("нет избранных валют"));
    }

    @Test
    public void testClearAllFavorites() {
        command.execute(CHAT_ID, "/fadd USD EUR");
        assertEquals(2, FavouritesCommand.getFavorites(CHAT_ID).size());
        command.execute(CHAT_ID, "/fadd clear");
        assertTrue(FavouritesCommand.getFavorites(CHAT_ID).isEmpty());
        assertTrue(sentMessages.get(CHAT_ID).contains("очищены"));
    }

    @Test
    public void testClearSpecificFavorites() {
        command.execute(CHAT_ID, "/fadd USD EUR GBP");
        assertEquals(3, FavouritesCommand.getFavorites(CHAT_ID).size());
        command.execute(CHAT_ID, "/fadd clear USD GBP");
        java.util.List<String> favorites = FavouritesCommand.getFavorites(CHAT_ID);
        assertEquals(1, favorites.size());
        assertTrue(favorites.contains("EUR"));
        assertFalse(favorites.contains("USD"));
        assertFalse(favorites.contains("GBP"));
    }

    @Test
    public void testMaxFavoritesLimit() {
        command.execute(CHAT_ID, "/fadd USD EUR GBP JPY CAD AUD CHF CNY NZD SGD");
        java.util.List<String> favorites = FavouritesCommand.getFavorites(CHAT_ID);
        assertEquals(10, favorites.size()); 
        sentMessages.clear(); 
        command.execute(CHAT_ID, "/fadd RUB");
        String response = sentMessages.get(CHAT_ID);
        assertTrue(response.contains("лимит") || response.contains("MAX_FAVOURITES") || response.contains("10"));
        assertEquals(10, FavouritesCommand.getFavorites(CHAT_ID).size());
    }

    @Test
    public void testDuplicateCurrencies() {
        command.execute(CHAT_ID, "/fadd USD EUR");
        command.execute(CHAT_ID, "/fadd USD GBP");
        java.util.List<String> favorites = FavouritesCommand.getFavorites(CHAT_ID);
        assertEquals(3, favorites.size()); 
        assertEquals(1, favorites.stream().filter(c -> c.equals("USD")).count());
    }

    @Test
    public void testClearNonExistingCurrencies() {
        command.execute(CHAT_ID, "/fadd USD");
        command.execute(CHAT_ID, "/fadd clear USD NONEXISTENT");
        assertTrue(FavouritesCommand.getFavorites(CHAT_ID).isEmpty());
        String response = sentMessages.get(CHAT_ID);
        assertTrue(response.contains("Удалено: USD"));
        assertTrue(response.contains("Не найдено: NONEXISTENT"));
    }
}