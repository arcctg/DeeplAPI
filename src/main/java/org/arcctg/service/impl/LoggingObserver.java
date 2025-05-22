package org.arcctg.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arcctg.service.api.EventData;
import org.arcctg.service.api.Observer;

@Slf4j
@RequiredArgsConstructor
public class LoggingObserver implements Observer {

    private final String name;

    @Override
    public void update(EventData eventData) {
        log.info("[{} Log] Event: {}. Details: {}",
            name,
            eventData.getClass().getSimpleName(),
            eventData.getDescription());
    }
}
