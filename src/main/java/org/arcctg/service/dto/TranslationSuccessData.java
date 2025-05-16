package org.arcctg.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.arcctg.service.api.EventData;

@AllArgsConstructor
@Getter
public class TranslationSuccessData implements EventData {
    private final String originalText;
    private final Object langPair;
    private final String translatedText;

    @Override
    public String getDescription() {
        return "Translation successful";
    }
}
