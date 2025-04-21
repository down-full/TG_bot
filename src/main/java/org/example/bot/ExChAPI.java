package org.example.bot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class ExChAPI implements CurrencyAPI {
    private final String API_KEY = "YOUR_API_KEY"; // Замените на ваш API ключ
    private final String BASE_URL = "https://api.exchangeratesapi.io/v1/latest";

    @Override
    public JSONObject getExchangeRates() {
        try {
            // Формируем URL для запроса
            String urlString = BASE_URL + "?access_key=" + API_KEY;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            // Проверяем код ответа
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            // Читаем ответ
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            conn.disconnect();

            // Преобразуем ответ в JSON объект
            return new JSONObject(response.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return null; // В случае ошибки возвращаем null
        }
    }
}

