package org.arcctg.deepl.model.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "quality",
    "regionalVariant",
    "mode",
    "browserType",
    "textType"
})
@Builder
public class CommonJobParams {

    @JsonProperty("quality")
    public String quality;
    @JsonProperty("regionalVariant")
    public String regionalVariant;
    @JsonProperty("mode")
    public String mode;
    @JsonProperty("browserType")
    public Integer browserType;
    @JsonProperty("textType")
    public String textType;

}
