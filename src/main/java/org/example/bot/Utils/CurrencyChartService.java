package org.example.bot.Utils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class CurrencyChartService {

    public static class HistoryPoint {
        public final String date;
        public final double rate;

        public HistoryPoint(String date, double rate) {
            this.date = date;
            this.rate = rate;
        }
    }

    public static List<HistoryPoint> getChartData(
            String baseCurrency,
            String targetCurrency,
            LocalDate from,
            LocalDate to
    ) throws Exception {
        return getChartData(baseCurrency, targetCurrency, from, to, new FreeCurrencyAPIImpl());
    }

    public static List<HistoryPoint> getChartData(
            String baseCurrency,
            String targetCurrency,
            LocalDate from,
            LocalDate to,
            CurrencyAPI api
    ) throws Exception {

        long days = ChronoUnit.DAYS.between(from, to);
        int stepDays = takeStep(days);

        List<HistoryPoint> points = new ArrayList<>();
        LocalDate date = from;

        while (!date.isAfter(to)) {
            Map<String, Double> rates = api.getHistorical(baseCurrency, date.toString());
            Double rate = rates.get(targetCurrency);
            if (rate != null) {
                points.add(new HistoryPoint(date.toString(), rate));
            }
            date = date.plusDays(stepDays);
        }

        if (!points.isEmpty()) {
            String endDate = to.toString();
            Map<String, Double> last = api.getHistorical(baseCurrency, endDate);
            if (last.containsKey(targetCurrency)) {
                HistoryPoint lastPoint = new HistoryPoint(endDate, last.get(targetCurrency));
                if (!points.get(points.size() - 1).date.equals(endDate)) {
                    points.add(lastPoint);
                }
            }
        }

        return points;
    }

    private static int takeStep(long days) {
        if (days <= 31) return 3;
        if (days <= 90) return 3;
        if (days <= 365) return 30;
        if (days <= 365 * 3) return 90;
        return 180;
    }
}

