package org.example.bot.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;



public class FreeCurrencyApi {

    private static final String API_KEY = System.getenv("CURRENCY_API_KEY");
    private static final String BASE_URL = "https://api.freecurrencyapi.com/v1/latest";
    private static final String HIST_URL = "https://api.freecurrencyapi.com/v1/historical";

    private static long lastUpdate = 0;
    private static final long CACHE_DURATION = 10 * 60 * 1000;
    private static Map<String, Double> cacheRates = null;

    private static final Map<String, Map<String, Double>> historicalCache = new HashMap<>();

    private static Map<String, Double> fetchRates(String url, 
                                                  RateExtractor extractor)
                                                  throws Exception {
        HttpURLConnection conn =
                (HttpURLConnection) new URI(url).toURL().openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("API error: " + conn.getResponseCode());
        }

        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(conn.getInputStream()))) {

            String jsonResponse = reader.lines().reduce("", String::concat);
            JSONObject root = new JSONObject(jsonResponse);

            JSONObject ratesObject = extractor.extract(root);

            Map<String, Double> rates = new HashMap<>();
            for (String currency : ratesObject.keySet()) {
                rates.put(currency, ratesObject.getDouble(currency));
            }
            return rates;
        }
    }

    private static Map<String, Double> getHistoricalRates(String baseCurrency,
                                                          String date) 
                                                          throws Exception {
        String url = HIST_URL 
                + "?apikey=" + API_KEY 
                + "&date=" + date 
                + "&base_currency=" + baseCurrency;

        return fetchRates(url, root ->
                root.getJSONObject("data").getJSONObject(date)
        );
    }

    public static Map<String, Double> getHistorical(String baseCurrency,
                                                    String date)
                                                    throws Exception {
        String key = baseCurrency + "_" + date;
        System.out.println("Запрос от " + key);

        if (historicalCache.containsKey(key)) {
            System.out.println("Historical cache used for " + key);
            return historicalCache.get(key);
        }

        Map<String, Double> rates = getHistoricalRates(baseCurrency, date);
        historicalCache.put(key, rates);
        return rates;
    }

    public static Map<String, Double> getRubRates() throws Exception {
        if (isCacheValid()) {
            System.out.println("Используется актуальный кэш");
            return new HashMap<>(cacheRates);
        }

        System.out.println("Берем из API");

        Map<String, Double> usdRates = getUsdRates();
        double rubPerUsd = usdRates.get("RUB");

        Map<String, Double> rubRates = new HashMap<>();
        for (Map.Entry<String, Double> entry : usdRates.entrySet()) {
            rubRates.put(entry.getKey(), rubPerUsd / entry.getValue());
        }

        cacheRates = new HashMap<>(rubRates);
        lastUpdate = System.currentTimeMillis();
        return rubRates;
    }

    private static boolean isCacheValid() {
        return cacheRates != null 
               && (System.currentTimeMillis() - lastUpdate) < CACHE_DURATION;
    }

    private static Map<String, Double> getUsdRates() throws Exception {
        String url = BASE_URL + "?apikey=" + API_KEY + "&base_currency=USD";

        return fetchRates(url, root -> root.getJSONObject("data"));
    }

    public static String normalizeCurrencyCode(String code) {
        code = code.toUpperCase();
        return switch (code) {
            case "RU", "RUR" -> "RUB";
            case "EURO" -> "EUR";
            case "DOLLAR", "$" -> "USD";
            default -> code;
        };
    }
}
