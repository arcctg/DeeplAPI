package org.arcctg.service.impl;

import org.arcctg.deepl.model.SourceTargetLangs;
import org.arcctg.service.api.TranslationService;

public abstract class BaseTranslationServiceDecorator implements TranslationService {

    private final TranslationService translationService;

    BaseTranslationServiceDecorator(TranslationService translationService) {
        this.translationService = translationService;
    }

    @Override
    public String process(String text, SourceTargetLangs langPair) {
        return translationService.process(text, langPair);
    }
}
