package org.example.bot;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class CurrencyConverterBotTest {

    @Mock
    private CurrencyAPI mockCurrencyApi; // Мокируем API для получения курсов валют

    @Mock
    private JSONObject mockRates; // Мокируем курсы валют


    @Mock
    private Update update;

    @Mock
    private Message message;

    @Mock
    private SendMessage sendMessage;

    private CurrencyConverterBot bot;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        bot = new CurrencyConverterBot();

        when(mockCurrencyApi.getExchangeRates()).thenReturn(mockRates);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.getText()).thenReturn("/start");
    }

    @Test

    public void testOnStartCommand(){

        long chatId = 12345L;
        bot.onUpdateReceived(update);

        verify(bot).sendResponse(eq(chatId),eq("Добро пожаловать в Currency Converter Bot!\n"));
        
    }

    public void onUpdateReceived() {
    }
}