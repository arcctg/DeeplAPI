package org.arcctg.service.impl;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.arcctg.service.api.RequestHandler;

public abstract class BaseRequestHandlerDecorator implements RequestHandler {

    private final RequestHandler requestHandler;

    BaseRequestHandlerDecorator(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    public HttpResponse<String> sendRequest(HttpRequest request) {
        return requestHandler.sendRequest(request);
    }
}
