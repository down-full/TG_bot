package org.example.bot;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class FreeCurrencyAPI {
    private static final String API_KEY = System.getenv("CURRENCY_API_KEY");
    private static final String BASE_URL = "https://api.freecurrencyapi.com/v1/latest";
    private static long lastUpdate = 0;
    private static final long CACHE_DURATION = 24 * 60 * 1000;
    private static Map <String, Double> cacheRates = null;


    public static Map<String, Double> getRubRates() throws Exception {

       if(isCacheValid()){
           System.out.println("Используется актуальный кэш");
           return new HashMap<>(cacheRates);
       }

       System.out.println("Берем из API");
        Map<String, Double> usdRates = getUSDRates();
        double rubPerUsd = usdRates.get("RUB");

        Map<String, Double> rubRates = new HashMap<>();
        for (Map.Entry<String, Double> entry : usdRates.entrySet()) {
            String currency = entry.getKey();
            double rateUsd = entry.getValue();
            rubRates.put(currency, rubPerUsd / rateUsd);
        }
        cacheRates = new HashMap<>(rubRates);
        lastUpdate = System.currentTimeMillis();

        return rubRates;
    }

    private static boolean isCacheValid(){
        return ((System.currentTimeMillis() - lastUpdate) < CACHE_DURATION && (cacheRates != null));
    }

    private static Map<String, Double> getUSDRates() throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URI(BASE_URL + "?apikey=" + API_KEY + "&base_currency=USD").toURL().openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("API error: " + conn.getResponseCode());
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String jsonResponse = reader.lines().reduce("", String::concat);
            JSONObject data = new JSONObject(jsonResponse).getJSONObject("data");

            Map<String, Double> rates = new HashMap<>();
            for (String currency : data.keySet()) {
                rates.put(currency, data.getDouble(currency));
            }
            return rates;
        }

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