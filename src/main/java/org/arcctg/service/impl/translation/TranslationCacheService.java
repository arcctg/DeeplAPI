package org.arcctg.service.impl.translation;

import lombok.extern.slf4j.Slf4j;
import org.arcctg.cache.impl.LRUCache;
import org.arcctg.cache.model.TranslationCacheKey;
import org.arcctg.cache.spi.Cache;
import org.arcctg.deepl.model.SourceTargetLangs;
import org.arcctg.service.api.TranslationService;

@Slf4j
public class TranslationCacheService implements TranslationService {

    private final TranslationService translationService;
    private final Cache<TranslationCacheKey, String> translationCache;

    private static final int DEFAULT_TRANSLATION_CACHE_SIZE = 100;

    public TranslationCacheService(TranslationService translationService) {
        this(translationService, DEFAULT_TRANSLATION_CACHE_SIZE);
    }

    public TranslationCacheService(TranslationService translationService,
        int translationCacheSize) {
        this.translationService = translationService;
        this.translationCache = new LRUCache<>(translationCacheSize);
    }

    @Override
    public String process(String text, SourceTargetLangs langPair) {
        String translation = getCachedTranslation(text, langPair);

        if (translation != null) {
            log.info("Using cached translation for text: {}", text);
        } else {
            translation = translationService.process(text, langPair);
            cacheTranslation(text, langPair, translation);
        }

        return translation;
    }

    private String getCachedTranslation(String text, SourceTargetLangs langPair) {
        TranslationCacheKey key = new TranslationCacheKey(text, langPair);
        return translationCache.get(key);
    }

    private void cacheTranslation(String text, SourceTargetLangs langPair, String translation) {
        TranslationCacheKey key = new TranslationCacheKey(text, langPair);
        translationCache.put(key, translation);
    }
}
