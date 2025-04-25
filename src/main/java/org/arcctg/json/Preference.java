package org.arcctg.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "weight",
    "default"
})
@Builder
public class Preference {

    @JsonProperty("weight")
    public Weight weight;
    @JsonProperty("default")
    public String _default;

}
