package com.saurav.scraper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

public class TitleTranslator {

    private static final String API_URL = "https://rapid-translate-multi-traduction.p.rapidapi.com/t";
    private static final String API_HOST = "rapid-translate-multi-traduction.p.rapidapi.com";
    private static final String API_KEY = "9fa984d028mshf86eb762ce65743p15ddb1jsnc38e1183aedf";

    public static List<String> getEnglishTitles(List<String> spanishTitles) {
        List<String> translatedTitles = new ArrayList<>();

        try {
            // Prepare request body
            String jsonPayload = new StringBuilder()
                .append("{")
                .append("\"from\":\"es\",")
                .append("\"to\":\"en\",")
                .append("\"e\":\"\",")
                .append("\"q\":")
                .append(new JSONArray(spanishTitles).toString())
                .append("}")
                .toString();

            // Build request
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("x-rapidapi-host", API_HOST)
                .header("x-rapidapi-key", API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

            // Send request
            HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

            // Parse response (it's a raw JSON array)
            JSONArray translatedArray = new JSONArray(response.body());

            for (int i = 0; i < translatedArray.length(); i++) {
                translatedTitles.add(translatedArray.getString(i));
            }

            // Optional: Print for debug
          //  System.out.println("Translated Titles:");
          //  translatedTitles.forEach(System.out::println);

        } catch (IOException | InterruptedException e) {
            System.err.println("Translation API failed: " + e.getMessage());
        }

        return translatedTitles;
    }
}
