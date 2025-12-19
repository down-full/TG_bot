package org.example.bot.utils;

import java.util.Map;

public interface CurrencyApi {
    Map<String, Double> getHistorical(String baseCurrency, String date) throws Exception;
}
