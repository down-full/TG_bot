package org.example.bot.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

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

        List<Date> xdates = points.stream()
                .map(p -> convert(LocalDate.parse(p.date)))
                .collect(Collectors.toList());

        List<Double> yvalues = points.stream()
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

        chart.addSeries(baseCurrency + "/" + targetCurrency, xdates, yvalues);

        BitmapEncoder.saveBitmap(chart, outputFile, BitmapEncoder.BitmapFormat.PNG);

        System.out.println("График сохранён в: " + outputFile + ".png");
    }
}
