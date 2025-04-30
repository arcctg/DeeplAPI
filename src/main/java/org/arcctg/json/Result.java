package org.arcctg.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "lang",
    "texts",
    "translations"
})
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Getter
public class Result {

    @JsonProperty("lang")
    public Lang lang;
    @JsonProperty("texts")
    public List<Text> texts;
    @JsonProperty("translations")
    public List<Translation> translations;

}
