package org.arcctg.deepl.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Language {
    ENGLISH("EN"),
    UKRAINIAN("UK");

    private final String code;

    @Override
    public String toString() {
        return code;
    }
}
