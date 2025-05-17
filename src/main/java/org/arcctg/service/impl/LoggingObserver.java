package org.arcctg.service.impl;

import lombok.AllArgsConstructor;
import org.arcctg.service.api.EventData;
import org.arcctg.service.api.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AllArgsConstructor
public class LoggingObserver implements Observer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String name;

    @Override
    public void update(EventData eventData) {
        logger.info("[{} Log] Event: {}. Details: {}",
            name,
            eventData.getClass().getSimpleName(),
            eventData.getDescription());
    }
}
