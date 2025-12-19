package org.example.bot.utils;

import java.util.Map;
import org.example.bot.api.FreeCurrencyApi;

public class FreeCurrencyApiImpl implements CurrencyApi {

    @Override
    public Map<String, Double> getHistorical(String baseCurrency, String date) throws Exception {
        return FreeCurrencyApi.getHistorical(baseCurrency, date);
    }
}
