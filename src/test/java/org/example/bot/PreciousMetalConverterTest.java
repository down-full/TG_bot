package org.example.bot;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class PreciousMetalConverterTest {

    private PreciousMetalConverter converter;
    private JSONObject rates;

    @Before
    public void setUp() {
        // тестовые данные
        rates = new JSONObject();
        rates.put("gold", 1.0);  // курс золота 1
        rates.put("silver", 0.8);  // курс серебра 0.8

        // Инициализируем конвертер
        converter = new PreciousMetalConverter(rates);
    }

    @Test
    public void testConvertGoldToSilver() {
        // Проверяем конвертацию из золота в серебро
        double result = converter.convert("gold", "silver", 10);
        assertEquals(12.5, result, 0.01);  // 10 * (0.8 / 1) = 12.5
    }

    @Test
    public void testConvertSilverToGold() {
        // Проверяем конвертацию из серебра в золото
        double result = converter.convert("silver", "gold", 10);
        assertEquals(8.0, result, 0.01);  // 10 * (1 / 0.8) = 8.0
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertUnsupportedMetal() {
        // Проверяем, что выбрасывается исключение, если металл не поддерживается
        converter.convert("diamond", "gold", 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertNegativeAmount() {
        // Проверяем, что выбрасывается исключение при отрицательной сумме
        converter.convert("gold", "silver", -10);
    }

    @Test(expected = ArithmeticException.class)
    public void testConvertWithZeroRate() {
        // Проверяем, что выбрасывается исключение, если курс металла равен нулю
        rates.put("platinum", 0.0);  // Устанавливаем курс платины равным 0
        converter = new PreciousMetalConverter(rates);
        converter.convert("platinum", "gold", 10);
    }
}
