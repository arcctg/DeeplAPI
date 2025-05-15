package org.arcctg.util.handler.impl;

import lombok.SneakyThrows;
import org.arcctg.util.handler.api.RequestHandler;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DefaultRequestHandler implements RequestHandler {

    private final HttpClient client;

    public DefaultRequestHandler() {
        client = HttpClient.newBuilder().build();
    }

    @SneakyThrows
    @Override
    public HttpResponse<String> sendRequest(HttpRequest request) {
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
