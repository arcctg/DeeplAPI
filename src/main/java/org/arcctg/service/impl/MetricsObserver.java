package org.arcctg.service.impl;

import org.arcctg.service.api.EventData;
import org.arcctg.service.api.Observer;
import org.arcctg.service.dto.TranslationAttemptData;
import org.arcctg.service.dto.TranslationFailureData;
import org.arcctg.service.dto.TranslationSuccessData;

public class MetricsObserver implements Observer {
    private int successCount = 0;
    private int failureCount = 0;
    private int attemptCount = 0;

    @Override
    public void update(EventData eventData) {
        if (eventData instanceof TranslationAttemptData) {
            System.out.println("[Metrics] New translation attempt. Total attempts: " + ++attemptCount);
        } else if (eventData instanceof TranslationSuccessData) {
            System.out.println("[Metrics] Translation successful. Success count: " + ++successCount);
        } else if (eventData instanceof TranslationFailureData) {
            System.out.println("[Metrics] Translation failed. Failure count: " + ++failureCount);
        }
    }

    public void printStats() {
        System.out.printf("[Metrics] Final Stats - Attempts: %d, Successes: %d, Failures: %d%n",
                attemptCount, successCount, failureCount);
    }
}
