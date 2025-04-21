package org.example.bot;

import org.json.JSONObject;

public abstract class AbstractCurrencyConverter {
    protected JSONObject rates;

    public AbstractCurrencyConverter(JSONObject rates) {
        this.rates = rates;
    }

    public abstract double convert(String fromCurrency, String toCurrency, double amount) throws IllegalArgumentException, ArithmeticException;
}
