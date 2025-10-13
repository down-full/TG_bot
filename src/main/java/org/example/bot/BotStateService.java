package org.example.bot;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;


public class BotStateService {

    public enum BotState {
        NONE,
        AWAITING_ARGS
    }

    private static final Map<Long, BotState> userStates = new ConcurrentHashMap<>();

    public static void setState(Long chatId, BotState state) {
        userStates.put(chatId, state);
    }

    public static BotState getState(Long chatId) {
        return userStates.getOrDefault(chatId, BotState.NONE);
    }

    public static void clearState(Long chatId) {
        userStates.remove(chatId);
    }
}
