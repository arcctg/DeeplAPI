package org.arcctg.service.impl;

import com.google.inject.Singleton;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import lombok.SneakyThrows;
import org.arcctg.service.api.RequestHandlerService;

@Singleton
public class DefaultRequestHandler implements RequestHandlerService {

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
