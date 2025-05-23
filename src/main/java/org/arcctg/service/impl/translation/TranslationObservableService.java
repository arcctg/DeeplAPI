package org.arcctg.service.impl.translation;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arcctg.deepl.model.SourceTargetLangs;
import org.arcctg.service.api.EventData;
import org.arcctg.service.api.Observer;
import org.arcctg.service.api.Subject;
import org.arcctg.service.api.TranslationService;

@Slf4j
@RequiredArgsConstructor
public class TranslationObservableService implements TranslationService, Subject {

    private final TranslationService translationService;
    private final List<Observer> observers = new ArrayList<>();

    @Override
    public String process(String text, SourceTargetLangs langPair) {
        notify(new TranslationAttemptData());

        try {
            String result = translationService.process(text, langPair);

            notify(new TranslationSuccessData());

            return result;
        } catch (Exception e) {
            notify(new TranslationFailureData());
            throw new RuntimeException("Translation failed " + e.getMessage());
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
