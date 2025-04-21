package org.example.bot;

import org.json.JSONObject;

public class StandardCurrencyConverter extends AbstractCurrencyConverter {

    public StandardCurrencyConverter(JSONObject rates) {
        super(rates);
    }

    @Override
    public double convert(String fromCurrency, String toCurrency, double amount) throws IllegalArgumentException, ArithmeticException {
        // Проверка на наличие валют в курсах
        if (!rates.has(fromCurrency)) {

            throw new IllegalArgumentException("Валюта " + fromCurrency + " не поддерживается.");
        }
        if (!rates.has(toCurrency)) {

            throw new IllegalArgumentException("Валюта " + toCurrency + " не поддерживается.");
        }
        if (amount < 0) {

            throw new IllegalArgumentException("Сумма не может быть отрицательной.");
        }

        // Получаем курсы валют
        double fromRate = rates.getDouble(fromCurrency);
        double toRate = rates.getDouble(toCurrency);

        // Проверяем, чтобы не произошло деление на ноль
        if (fromRate == 0 ) {

            throw new ArithmeticException("Курс валюты " + fromCurrency + " равен нулю, невозможно конвертировать.");
        }

        if(toRate == 0){

            throw new ArithmeticException(("Курс валюты " + toCurrency + " равен нулю, невозможно конвертировать."));
        }

        // Выполняем конвертацию
        return amount * (fromRate / toRate);
    }
}
