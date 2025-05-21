package org.arcctg.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.arcctg.service.api.EventData;
import org.arcctg.service.api.Observer;
import org.arcctg.service.dto.TranslationAttemptData;
import org.arcctg.service.dto.TranslationFailureData;
import org.arcctg.service.dto.TranslationSuccessData;

@Slf4j
public class MetricsObserver implements Observer {

    private int successCount = 0;
    private int failureCount = 0;
    private int attemptCount = 0;

    @Override
    public void update(EventData eventData) {
        if (eventData instanceof TranslationAttemptData) {
            log.info("[Metrics] New translation attempt. Total attempts: {}", ++attemptCount);
        } else if (eventData instanceof TranslationSuccessData) {
            log.info("[Metrics] Translation successful. Success count: {}", ++successCount);
        } else if (eventData instanceof TranslationFailureData) {
            log.info("[Metrics] Translation failed. Failure count: {}", ++failureCount);
        }
    }

    public void printStats() {
        log.info("[Metrics] Final Stats - Attempts: {}, Successes: {}, Failures: {}",
            attemptCount, successCount, failureCount);
    }
}
