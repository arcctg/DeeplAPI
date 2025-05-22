package org.arcctg.service.impl;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arcctg.deepl.model.SourceTargetLangs;
import org.arcctg.service.api.EventData;
import org.arcctg.service.api.Observer;
import org.arcctg.service.api.Subject;
import org.arcctg.service.api.TranslationService;
import org.arcctg.service.dto.TranslationAttemptData;
import org.arcctg.service.dto.TranslationFailureData;
import org.arcctg.service.dto.TranslationSuccessData;

@Slf4j
@RequiredArgsConstructor
public class TranslationObservableService implements TranslationService, Subject {

    private final TranslationService translationService;
    private final List<Observer> observers = new ArrayList<>();

    @Override
    public String process(String text, SourceTargetLangs langPair) {
        notify(new TranslationAttemptData(text, langPair));

        try {
            String result = translationService.process(text, langPair);

            notify(new TranslationSuccessData(text, langPair, result));

            return result;
        } catch (Exception e) {
            notify(new TranslationFailureData(text, langPair, e));
            log.warn("TranslationObservableService: Caught exception, Error: {}", e.getMessage());

            return "";
        }
    }

    @Override
    public void subscribe(Observer observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void unsubscribe(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notify(EventData eventData) {
        new ArrayList<>(observers).forEach(observer -> observer.update(eventData));
    }
}
