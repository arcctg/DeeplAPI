package org.arcctg.deepl.client;

import lombok.Getter;
import org.arcctg.deepl.model.SourceTargetLangs;
import org.arcctg.service.api.TranslationService;

public class DeeplClient {

    @Getter
    private final TranslationService translationService;

    protected DeeplClient(TranslationService translationService) {
        this.translationService = translationService;
    }

    public String getAlternativesAtPosition(int position) {
        return "";
    }

    public String translate(String text, SourceTargetLangs langPair) {
        return translationService.process(text, langPair);
    }

    public static DeeplClientBuilder builder() {
        return new DeeplClientBuilder();
    }
}
