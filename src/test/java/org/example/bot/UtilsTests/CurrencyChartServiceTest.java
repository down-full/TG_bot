package org.example.bot.UtilsTests;

import org.example.bot.Utils.CurrencyAPI;
import org.example.bot.Utils.CurrencyChartService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.time.temporal.ChronoUnit;
import static org.junit.jupiter.api.Assertions.*;

class CurrencyChartServiceTest {

    @Test
    void testGetChartDataSmallRange() throws Exception {
        CurrencyAPI fakeApi = new CurrencyAPI() {
            @Override
            public Map<String, Double> getHistorical(String baseCurrency, String date) {
                // возвращаем разные значения для конкретных дат
                return switch (date) {
                    case "2025-01-01" -> Map.of("EUR", 0.9);
                    case "2025-01-04" -> Map.of("EUR", 0.91);
                    case "2025-01-10" -> Map.of("EUR", 0.92);
                    default -> Map.of();
                };
            }
        };

        LocalDate from = LocalDate.of(2025, 1, 1);
        LocalDate to = LocalDate.of(2025, 1, 10);

        List<CurrencyChartService.HistoryPoint> points =
                CurrencyChartService.getChartData("USD","EUR", from, to, fakeApi);

        assertEquals(3, points.size());
        assertEquals("2025-01-01", points.get(0).date);
        assertEquals(0.9, points.get(0).rate);
        assertEquals("2025-01-04", points.get(1).date);
        assertEquals(0.91, points.get(1).rate);
        assertEquals("2025-01-10", points.get(2).date);
        assertEquals(0.92, points.get(2).rate);
    }

    @Test
    void testEmptyResponse() throws Exception {
        CurrencyAPI fakeApi = new CurrencyAPI() {
            @Override
            public Map<String, Double> getHistorical(String baseCurrency, String date) {
                return Map.of(); // пустой ответ API
            }
        };

        LocalDate from = LocalDate.of(2025, 1, 1);
        LocalDate to = LocalDate.of(2025, 1, 5);

        List<CurrencyChartService.HistoryPoint> points =
                CurrencyChartService.getChartData("USD","EUR", from, to, fakeApi);

        assertTrue(points.isEmpty());
    }

    @Test
    void testSingleDay() throws Exception {
        CurrencyAPI fakeApi = new CurrencyAPI() {
            @Override
            public Map<String, Double> getHistorical(String baseCurrency, String date) {
                return Map.of("EUR", 0.95);
            }
        };

        LocalDate date = LocalDate.of(2025, 2, 2);

        List<CurrencyChartService.HistoryPoint> points =
                CurrencyChartService.getChartData("USD","EUR", date, date, fakeApi);

        assertEquals(1, points.size());
        assertEquals("2025-02-02", points.get(0).date);
        assertEquals(0.95, points.get(0).rate);
    }

    @Test
    void testAddLastPointIfMissing() throws Exception {
        CurrencyAPI fakeApi = new CurrencyAPI() {
            @Override
            public Map<String, Double> getHistorical(String baseCurrency, String date) {
                // шаг = 3, конечная дата 2025-01-10 не входит в цикл
                return Map.of("EUR", date.equals("2025-01-10") ? 0.92 : 0.9);
            }
        };

        LocalDate from = LocalDate.of(2025, 1, 1);
        LocalDate to = LocalDate.of(2025, 1, 10);

        List<CurrencyChartService.HistoryPoint> points =
                CurrencyChartService.getChartData("USD","EUR", from, to, fakeApi);

        assertEquals(4, points.size());
        assertEquals("2025-01-10", points.get(3).date);
        assertEquals(0.92, points.get(3).rate);
    }

    @Test
    void testMediumRangeStep30() throws Exception {
        CurrencyAPI fakeApi = new CurrencyAPI() {
            @Override
            public Map<String, Double> getHistorical(String baseCurrency, String date) {
                // возвращаем курс = 1 + номер месяца * 0.01
                int month = LocalDate.parse(date).getMonthValue();
                return Map.of("EUR", 1.0 + month * 0.01);
            }
        };

        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 12, 31);

        List<CurrencyChartService.HistoryPoint> points =
                CurrencyChartService.getChartData("USD","EUR", from, to, fakeApi);

        // проверяем, что первая и последняя точка корректны
        assertEquals("2024-01-01", points.get(0).date);
        assertEquals(1.01, points.get(0).rate);

        assertEquals("2024-12-31", points.get(points.size()-1).date);

        // проверяем, что шаг соответствует ожиданиям
        for (int i = 1; i < points.size(); i++) {
            LocalDate prev = LocalDate.parse(points.get(i-1).date);
            LocalDate curr = LocalDate.parse(points.get(i).date);
            long daysBetween = ChronoUnit.DAYS.between(prev, curr);

            // последняя точка может быть меньше шага
            assertTrue(daysBetween == 30 || i == points.size() - 1);
        }
    }
}

