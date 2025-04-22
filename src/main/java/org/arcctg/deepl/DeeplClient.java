package org.arcctg.deepl;

import java.net.http.HttpClient;
import java.util.List;

public class DeeplClient {
    private static final String API_URL = "https://www2.deepl.com/jsonrpc";
    private final HttpClient client;
    private Long id = 100_000L;

    public DeeplClient() {
        this.client = HttpClient.newBuilder().build();
    }

    public String getAlternativesAtPosition(int position) {
        return "";
    }

    public String translate(String text, SourceTargetLangs sourceTargetLangs) {
        return "";
    }

    private List<String> splitText(String text) {
        return List.of();
    }
}
