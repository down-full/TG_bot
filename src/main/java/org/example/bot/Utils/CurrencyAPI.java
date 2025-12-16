package org.example.bot.Utils;

import java.util.Map;

public interface CurrencyAPI {
    Map<String, Double> getHistorical(String baseCurrency, String date) throws Exception;
}
