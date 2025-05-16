package org.arcctg.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.arcctg.service.api.EventData;

@AllArgsConstructor
@Getter
public class TranslationAttemptData implements EventData {
    private final String text;
    private final Object langPair;

    @Override
    public String getDescription() {
        return "Attempting translation";
    }
}
