package org.arcctg.deepl.client;

import org.arcctg.service.api.TranslationService;
import org.arcctg.service.impl.TranslationSyncService;
import org.arcctg.deepl.model.SourceTargetLangs;

public class DeeplClient {
    private final TranslationService translationService;

    public DeeplClient() {
        this(new TranslationSyncService());
    }

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
