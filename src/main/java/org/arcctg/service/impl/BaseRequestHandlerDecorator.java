package org.arcctg.service.impl;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.arcctg.service.api.RequestHandlerService;

public abstract class BaseRequestHandlerDecorator implements RequestHandlerService {

    private final RequestHandlerService requestHandler;

    BaseRequestHandlerDecorator(RequestHandlerService requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    public HttpResponse<String> sendRequest(HttpRequest request) {
        return requestHandler.sendRequest(request);
    }
}
