package org.arcctg.cache.translation;

import org.arcctg.cache.Cache;
import org.arcctg.cache.LRUCache;
import org.arcctg.deepl.model.SourceTargetLangs;

public class TranslationCacheService {
    private static final int DEFAULT_TRANSLATION_CACHE_SIZE = 100;

    private final Cache<TranslationCacheKey, String> translationCache;

    public TranslationCacheService() {
        this(DEFAULT_TRANSLATION_CACHE_SIZE);
    }

    public TranslationCacheService(int translationCacheSize) {
        this.translationCache = new LRUCache<>(translationCacheSize);
    }

    public String getCachedTranslation(String text, SourceTargetLangs langPair) {
        TranslationCacheKey key = new TranslationCacheKey(text, langPair);
        return translationCache.get(key);
    }

    public void cacheTranslation(String text, SourceTargetLangs langPair, String translation) {
        TranslationCacheKey key = new TranslationCacheKey(text, langPair);
        translationCache.put(key, translation);
    }

    public void clearCaches() {
        translationCache.clear();
    }

    public int getTranslationCacheSize() {
        return translationCache.size();
    }
}