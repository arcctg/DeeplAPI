package org.arcctg.util.handler.impl;

import lombok.SneakyThrows;
import org.arcctg.util.handler.api.RequestHandler;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RetryRequestHandlerDecorator extends BaseRequestHandlerDecorator {

    private static final int DEFAULT_MAX_RETRIES = 1;
    private static final long DEFAULT_RETRY_DELAY_MS = 1000;

    private final int maxRetries;
    private final long retryDelayMs;

    public RetryRequestHandlerDecorator() {
        this(new DefaultRequestHandler(), DEFAULT_MAX_RETRIES, DEFAULT_RETRY_DELAY_MS);
    }

    public RetryRequestHandlerDecorator(int maxRetries, long retryDelayMs) {
        this(new DefaultRequestHandler(), maxRetries, retryDelayMs);
    }

    public RetryRequestHandlerDecorator(RequestHandler requestHandler, int maxRetries, long retryDelayMs) {
        super(requestHandler);
        this.maxRetries = maxRetries;
        this.retryDelayMs = retryDelayMs;
    }

    @SneakyThrows
    @Override
    public HttpResponse<String> sendRequest(HttpRequest request) {
        int attempts = 0;

        while (attempts <= maxRetries) {
            try {
                return super.sendRequest(request);
            } catch (Exception e) {
                if (++attempts > maxRetries) {
                    throw new RuntimeException("Failed after " + maxRetries + " retry attempts", e);
                }
                Thread.sleep(retryDelayMs);
            }
        }

        throw new RuntimeException("Unexpected error");
    }
}
