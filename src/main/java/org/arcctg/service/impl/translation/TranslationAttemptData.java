package org.arcctg.service.impl.translation;

import org.arcctg.service.api.EventData;

public class TranslationAttemptData implements EventData {

    @Override
    public String getDescription() {
        return "Attempting translation";
    }
}
