package org.arcctg.service.impl.request;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import lombok.RequiredArgsConstructor;
import org.arcctg.service.api.RequestHandlerService;

@RequiredArgsConstructor()
public abstract class BaseRequestHandlerDecorator implements RequestHandlerService {

    private final RequestHandlerService requestHandler;

    @Override
    public HttpResponse<String> sendRequest(HttpRequest request) {
        return requestHandler.sendRequest(request);
    }
}
