package org.arcctg.service.impl;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arcctg.service.api.RequestHandlerService;

@Slf4j
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

    public RetryRequestHandlerDecorator(RequestHandlerService requestHandler, int maxRetries,
        long retryDelayMs) {
        super(requestHandler);
        this.maxRetries = maxRetries;
        this.retryDelayMs = retryDelayMs;
    }

    @SneakyThrows
    @Override
    public HttpResponse<String> sendRequest(HttpRequest request) {
        for (int attempts = 1; attempts <= maxRetries + 1; attempts++) {
            try {
                HttpResponse<String> response = super.sendRequest(request);
                if (response.statusCode() == 200) {
                    return response;
                }

                throw new RuntimeException(
                    "Error %d: %s".formatted(response.statusCode(), response.body()));
            } catch (Exception e) {
                if (attempts > maxRetries) {
                    throw new RuntimeException("Failed after " + maxRetries + " retry attempts", e);
                }
                log.warn("Attempt {} failed, retrying in {} ms", attempts, retryDelayMs, e);
                Thread.sleep(retryDelayMs);
            }
        }

        throw new RuntimeException("Unexpected error");
    }
}
