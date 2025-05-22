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

    public RetryRequestHandlerDecorator(RequestHandlerService requestHandler) {
        this(requestHandler, DEFAULT_MAX_RETRIES, DEFAULT_RETRY_DELAY_MS);
    }

    public RetryRequestHandlerDecorator(RequestHandlerService requestHandler, int maxRetries,
        long retryDelayMs) {
        super(requestHandler);
        this.maxRetries = maxRetries;
        this.retryDelayMs = retryDelayMs;
    }

    @Override
    public HttpResponse<String> sendRequest(HttpRequest request) {
        Exception lastException = null;

        for (int attempts = 1; attempts <= maxRetries + 1; attempts++) {
            try {
                HttpResponse<String> response = super.sendRequest(request);

                if (isSuccessResponse(response)) {
                    return response;
                }

                lastException = createHttpErrorException(response);
            } catch (Exception e) {
                lastException = e;
            }

            if (attempts > maxRetries) {
                break;
            }

            waitBeforeRetry(attempts);
        }

        throw new RuntimeException("Failed after " + maxRetries + " retry attempts", lastException);
    }

    private boolean isSuccessResponse(HttpResponse<String> response) {
        return response.statusCode() >= 200 && response.statusCode() < 300;
    }

    private RuntimeException createHttpErrorException(HttpResponse<String> response) {
        return new RuntimeException(
            "HTTP Error %d: %s".formatted(response.statusCode(), response.body()));
    }

    @SneakyThrows
    private void waitBeforeRetry(int attemptNumber) {
        log.warn("Attempt {} failed, retrying in {} ms", attemptNumber, retryDelayMs);
        Thread.sleep(retryDelayMs);
    }
}
