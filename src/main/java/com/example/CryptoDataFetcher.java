package com.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class CryptoDataFetcher {

    private static final String API_URL = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=5&page=1";

    private final HttpClient client;
    private final ObjectMapper mapper;

    public CryptoDataFetcher() {
        client = HttpClient.newHttpClient();
        mapper = new ObjectMapper();
    }

    public List<Crypto> fetchTop5() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode array = mapper.readTree(response.body());

        List<Crypto> cryptos = new ArrayList<>();
        for (JsonNode node : array) {
            Crypto crypto = new Crypto();
            crypto.name = node.get("name").asText();
            crypto.symbol = node.get("symbol").asText();
            crypto.price = node.get("current_price").asDouble();

            crypto.history = new ArrayList<>();
            // Ініціалізація історії демонстраційно
            for (int i = 0; i < 10; i++) {
                crypto.history.add(crypto.price - Math.random() * 5);
            }

            cryptos.add(crypto);
        }
        return cryptos;
    }

    public static class Crypto {
        public String name;
        public String symbol;
        public double price;
        public List<Double> history;
    }
}
