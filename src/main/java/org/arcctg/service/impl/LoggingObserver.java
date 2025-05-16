package org.arcctg.service.impl;

import lombok.AllArgsConstructor;
import org.arcctg.service.api.EventData;
import org.arcctg.service.api.Observer;

@AllArgsConstructor
public class LoggingObserver implements Observer {
    private final String name;

    @Override
    public void update(EventData eventData) {
        System.out.printf("[%s Log] Event: %s. Details: %s%n",
                name,
                eventData.getClass().getSimpleName(),
                eventData.getDescription());
    }
}
