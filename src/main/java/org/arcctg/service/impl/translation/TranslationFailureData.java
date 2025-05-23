package org.arcctg.service.impl.translation;

import org.arcctg.service.api.EventData;

public class TranslationFailureData implements EventData {

    @Override
    public String getDescription() {
        return "Translation failed";
    }
}
