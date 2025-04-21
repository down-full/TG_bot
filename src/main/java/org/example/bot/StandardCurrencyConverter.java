package org.example.bot;

import org.json.JSONObject;

public class StandardCurrencyConverter extends AbstractCurrencyConverter {

    public StandardCurrencyConverter(JSONObject rates) {
        super(rates);
    }

    @Override
    public double convert(String fromCurrency, String toCurrency, double amount) throws IllegalArgumentException, ArithmeticException {

        if (!rates.has(fromCurrency)) {

            throw new IllegalArgumentException("Валюта " + fromCurrency + " не поддерживается.");
        }
        if (!rates.has(toCurrency)) {

            throw new IllegalArgumentException("Валюта " + toCurrency + " не поддерживается.");
        }
        if (amount < 0) {

            throw new IllegalArgumentException("Сумма не может быть отрицательной.");
        }


        double fromRate = rates.getDouble(fromCurrency);
        double toRate = rates.getDouble(toCurrency);


        if (fromRate == 0 ) {

            throw new ArithmeticException("Курс валюты " + fromCurrency + " равен нулю, невозможно конвертировать.");
        }

        if(toRate == 0){

            throw new ArithmeticException(("Курс валюты " + toCurrency + " равен нулю, невозможно конвертировать."));
        }


        return amount * (fromRate / toRate);
    }
}
