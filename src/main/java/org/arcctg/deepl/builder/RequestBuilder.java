package org.arcctg.deepl.builder;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import lombok.SneakyThrows;

public class RequestBuilder {
    private static final String API_URL = "https://www2.deepl.com/jsonrpc";

    private RequestBuilder() {}

    @SneakyThrows
    public static HttpRequest buildDefault(String payload) {
        return HttpRequest.newBuilder()
            .uri(new URI(API_URL))
            .header("accept", "*/*")
            .header("accept-language", "en-UA,en;q=0.9")
            .header("content-type", "application/json")
            .header("dnt", "1")
            .header("origin", "https://www.deepl.com")
            .header("priority", "u=1, i")
            .header("referer", "https://www.deepl.com/")
            .header("sec-ch-ua",
                "Not(A:Brand\";v=\"99\", \"Google Chrome\";v=\"133\", \"Chromium\";v=\"133\"")
            .header("sec-ch-ua-mobile", "?0")
            .header("sec-ch-ua-platform", "\"Windows\"")
            .header("sec-fetch-dest", "empty")
            .header("sec-fetch-mode", "cors")
            .header("sec-fetch-site", "same-site")
            .header(
                "user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36")
            .POST(BodyPublishers.ofString(payload))
            .build();
    }
}
