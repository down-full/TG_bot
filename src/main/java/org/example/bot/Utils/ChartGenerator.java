package org.example.bot.Utils;

import org.knowm.xchart.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class ChartGenerator {

    private static Date convert(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static void generatePngChart(
            String baseCurrency,
            String targetCurrency,
            LocalDate from,
            LocalDate to,
            String outputFile
    ) throws Exception {

        List<CurrencyChartService.HistoryPoint> points =
                CurrencyChartService.getChartData(baseCurrency, targetCurrency, from, to);

        List<Date> xDates = points.stream()
                .map(p -> convert(LocalDate.parse(p.date)))
                .collect(Collectors.toList());

        List<Double> yValues = points.stream()
                .map(p -> p.rate)
                .collect(Collectors.toList());

        XYChart chart = new XYChartBuilder()
                .width(1200)
                .height(800)
                .title(baseCurrency + " → " + targetCurrency)
                .xAxisTitle("Date")
                .yAxisTitle("Rate")
                .build();

        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setXAxisLabelRotation(45);

        chart.addSeries(baseCurrency + "/" + targetCurrency, xDates, yValues);

        BitmapEncoder.saveBitmap(chart, outputFile, BitmapEncoder.BitmapFormat.PNG);

        System.out.println("График сохранён в: " + outputFile + ".png");
    }
}
