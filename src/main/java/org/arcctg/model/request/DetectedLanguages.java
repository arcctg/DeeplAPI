package org.arcctg.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "EN"
})
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Getter
public class DetectedLanguages {

    @JsonProperty("EN")
    public double en;

}
