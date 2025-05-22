package org.arcctg.service.impl;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.arcctg.service.api.RequestHandlerService;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class BaseRequestHandlerDecorator implements RequestHandlerService {

    private final RequestHandlerService requestHandler;

    @Override
    public HttpResponse<String> sendRequest(HttpRequest request) {
        return requestHandler.sendRequest(request);
    }
}
