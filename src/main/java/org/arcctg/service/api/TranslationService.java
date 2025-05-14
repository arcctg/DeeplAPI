package org.arcctg.service.api;

import org.arcctg.deepl.model.SourceTargetLangs;

public interface TranslationService {

    String process(String text, SourceTargetLangs langPair);

}
