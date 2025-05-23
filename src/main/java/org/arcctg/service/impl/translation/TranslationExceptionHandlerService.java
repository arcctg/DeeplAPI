package org.arcctg.service.impl.translation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arcctg.deepl.model.SourceTargetLangs;
import org.arcctg.service.api.TranslationService;

@Slf4j
@RequiredArgsConstructor
public class TranslationExceptionHandlerService implements TranslationService {

    private final TranslationService translationService;

    @Override
    public String process(String text, SourceTargetLangs langPair) {
        String result = "Translation failed";

        try {
            result = translationService.process(text, langPair);
        } catch (RuntimeException e) {
            log.warn("Translation exception, Error: {}", e.getMessage());
        } catch (Exception e) {
            log.warn("Unexpected exception, Error: {}", e.getMessage());
        }

        return result;
    }
}
