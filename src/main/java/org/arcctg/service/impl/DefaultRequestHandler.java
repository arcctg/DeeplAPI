package org.arcctg.service.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.arcctg.service.api.RequestHandlerService;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class DefaultRequestHandler implements RequestHandlerService {

    private final HttpClient client;

    @SneakyThrows
    @Override
    public HttpResponse<String> sendRequest(HttpRequest request) {
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
