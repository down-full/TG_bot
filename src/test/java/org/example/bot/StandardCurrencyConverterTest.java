package org.example.bot;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class StandardCurrencyConverterTest {

    private StandardCurrencyConverter converter;
    private JSONObject rates;

    @Before
    public void setup(){
        rates = new JSONObject();
        rates.put("USD", 1.0);
        rates.put("EUR",1.1);
        rates.put("GBP", 1.29);
        converter = new StandardCurrencyConverter(rates);

    }

    @Test
    public void testValidCurrencies() {

        double result = converter.convert("EUR", "USD", 100);
        assertEquals(110.0, result, 0.01);

        result = converter.convert("EUR", "GBP", 50);
        assertEquals(42.63, result, 0.01);
    }

    @Test
    public void testValidBackCurrencies() {

        double result = converter.convert("USD", "EUR", 100);
        assertEquals(90.90, result, 0.01);

        result = converter.convert("GBP", "EUR", 50);
        assertEquals(58.63, result, 0.01);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertNegativeCurrency(){

        converter.convert("USD", "RU", -134);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertUnsupportCurrency(){

        converter.convert("USD", "RU", 100);

    }

    @Test(expected = ArithmeticException.class)
    public void testConvertZeroRate(){

        rates.put("ZERO", 0.0);
        converter.convert("USD", "ZERO", 100);

    }

    @Test
    public void testConvertSameCurrency(){

        converter.convert("USD", "USD", 100);

    }
}