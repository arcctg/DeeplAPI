package org.arcctg.json;

import java.util.List;
import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "sentences",
    "num_symbols",
    "rephrase_variant"
})
@Generated("jsonschema2pojo")
@Getter
public class Beam {

    @JsonProperty("sentences")
    public List<Sentence> sentences;
    @JsonProperty("num_symbols")
    public Long numSymbols;
    @JsonProperty("rephrase_variant")
    public RephraseVariant rephraseVariant;

}
