package org.example.bot;

import org.json.JSONObject;

public class PreciousMetalConverter extends AbstractCurrencyConverter{
    public PreciousMetalConverter(JSONObject rates) {
        super(rates);
    }
    @Override
    public double convert(String fromMetal, String toMetal, double amount) throws IllegalArgumentException, ArithmeticException {

        if (!rates.has(fromMetal)) {
            throw new IllegalArgumentException("Металл " + fromMetal + " не поддерживается.");
        }
        if (!rates.has(toMetal)) {
            throw new IllegalArgumentException("Металл " + toMetal + " не поддерживается.");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Сумма не может быть отрицательной.");
        }


        double fromRate = rates.getDouble(fromMetal);
        double toRate = rates.getDouble(toMetal);


        if (fromRate == 0) {
            throw new ArithmeticException("Курс металла " + fromMetal + " равен нулю, невозможно конвертировать.");
        }


        return amount * (fromRate / toRate);
    }
}
