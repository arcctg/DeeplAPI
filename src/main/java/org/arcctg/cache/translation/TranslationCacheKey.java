package org.arcctg.cache.translation;

import java.util.Objects;
import org.arcctg.deepl.model.SourceTargetLangs;

public class TranslationCacheKey {
    private final String text;
    private final SourceTargetLangs langPair;

    public TranslationCacheKey(String text, SourceTargetLangs langPair) {
        this.text = text;
        this.langPair = langPair;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TranslationCacheKey that = (TranslationCacheKey) o;
        return Objects.equals(text, that.text) &&
            Objects.equals(langPair.getSourceLang(), that.langPair.getSourceLang()) &&
            Objects.equals(langPair.getTargetLang(), that.langPair.getTargetLang());
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, langPair.getSourceLang(), langPair.getTargetLang());
    }
}