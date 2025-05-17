package org.arcctg.deepl.model.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.function.Consumer;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "target_lang",
    "langPreference",
    "source_lang_computed",
    "lang_user_selected",
    "user_preferred_langs",
    "detected",
    "isConfident",
    "detectedLanguages"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lang {

    @JsonProperty("target_lang")
    public String targetLang;
    @JsonProperty("source_lang_computed")
    private String sourceLangComputed;
    @JsonProperty("lang_user_selected")
    private String langUserSelected;
    @JsonProperty("langPreference")
    public Preference preference;
    @JsonProperty("user_preferred_langs")
    public String[] userPreferredLangs;
    @JsonProperty("detected")
    public String detected;
    @JsonProperty("isConfident")
    public Boolean isConfident;
    @JsonProperty("detectedLanguages")
    public DetectedLanguages detectedLanguages;


    public static class LangBuilder {
        public LangBuilder preference(Consumer<Preference.PreferenceBuilder> consumer) {
            Preference.PreferenceBuilder builder = Preference.builder();
            consumer.accept(builder);
            this.preference = builder.build();

            return this;
        }
    }
}
