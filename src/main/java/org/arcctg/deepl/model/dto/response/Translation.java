package org.arcctg.deepl.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "beams",
    "quality"
})
@Getter
public class Translation {

    @JsonProperty("beams")
    public List<Beam> beams;
    @JsonProperty("quality")
    public String quality;

}
