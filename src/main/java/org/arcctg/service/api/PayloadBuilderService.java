package org.arcctg.service.api;

import java.util.List;
import org.arcctg.deepl.model.SourceTargetLangs;
import org.arcctg.deepl.model.dto.common.Sentence;

public interface PayloadBuilderService {
    String buildForTextSegmentation(String text);
    List<String> buildForAllSentences(List<Sentence> allSentences, SourceTargetLangs langPair);
}
