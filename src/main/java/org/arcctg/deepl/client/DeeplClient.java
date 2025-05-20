package org.arcctg.deepl.client;

import org.arcctg.deepl.model.SourceTargetLangs;
import org.arcctg.service.api.TranslationService;

public class DeeplClient {
    private final TranslationService translationService;

    public DeeplClient(TranslationService translationService) {
        this.translationService = translationService;
    }

    public String getAlternativesAtPosition(int position) {
        return "";
    }

    public String translate(String text, SourceTargetLangs langPair) {
        return translationService.process(text, langPair);
    }
}
