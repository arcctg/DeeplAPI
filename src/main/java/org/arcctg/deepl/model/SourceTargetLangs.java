package org.arcctg.deepl.model;

import lombok.Data;

@Data
public class SourceTargetLangs {
    private String sourceLang;
    private String targetLang;

    public SourceTargetLangs(Language sourceLang, Language targetLang) {
        this.sourceLang = sourceLang.toString();
        this.targetLang = targetLang.toString();
    }
}
