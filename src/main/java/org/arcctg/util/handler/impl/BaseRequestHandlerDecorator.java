package org.arcctg.util.handler.impl;

import org.arcctg.util.handler.api.RequestHandler;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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
