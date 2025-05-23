package org.arcctg.service.impl.translation;

import org.arcctg.service.api.EventData;

public class TranslationSuccessData implements EventData {

    @Override
    public String getDescription() {
        return "Translation successful";
    }
}
