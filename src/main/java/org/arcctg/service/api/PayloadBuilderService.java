package org.arcctg.service.api;

import com.google.inject.ImplementedBy;
import java.util.List;
import org.arcctg.deepl.model.SourceTargetLangs;
import org.arcctg.deepl.model.dto.common.Sentence;
import org.arcctg.service.impl.DefaultPayloadBuilderService;

@ImplementedBy(DefaultPayloadBuilderService.class)
public interface PayloadBuilderService {
    String buildForTextSegmentation(String text);
    List<String> buildForAllSentences(List<Sentence> allSentences, SourceTargetLangs langPair);
}
