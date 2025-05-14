package org.arcctg.deepl.service.interfaces;

import org.arcctg.deepl.model.SourceTargetLangs;

public interface TranslationService {

    String process(String text, SourceTargetLangs langPair);

}
