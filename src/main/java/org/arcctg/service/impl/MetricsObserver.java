package org.arcctg.service.impl;

import org.arcctg.service.api.EventData;
import org.arcctg.service.api.Observer;
import org.arcctg.service.dto.TranslationAttemptData;
import org.arcctg.service.dto.TranslationFailureData;
import org.arcctg.service.dto.TranslationSuccessData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricsObserver implements Observer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private int successCount = 0;
    private int failureCount = 0;
    private int attemptCount = 0;

    @Override
    public void update(EventData eventData) {
        if (eventData instanceof TranslationAttemptData) {
            logger.info("[Metrics] New translation attempt. Total attempts: {}", ++attemptCount);
        } else if (eventData instanceof TranslationSuccessData) {
            logger.info("[Metrics] Translation successful. Success count: {}", ++successCount);
        } else if (eventData instanceof TranslationFailureData) {
            logger.info("[Metrics] Translation failed. Failure count: {}", ++failureCount);
        }
    }

    public void printStats() {
        logger.info("[Metrics] Final Stats - Attempts: {}, Successes: {}, Failures: {}",
            attemptCount, successCount, failureCount);
    }
}
