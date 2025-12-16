package org.example.bot.Core;

import java.util.Map;

import org.example.bot.API.FreeCurrencyAPI;
import org.example.bot.Utils.CurrencyAPI;

public class FreeCurrencyAPIImpl implements CurrencyAPI {
    @Override
    public Map<String, Double> getHistorical(String baseCurrency, String date) throws Exception {
        return FreeCurrencyAPI.getHistorical(baseCurrency, date);
    }
}
