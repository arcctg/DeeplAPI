package org.arcctg.deepl.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.arcctg.deepl.model.dto.common.Sentence;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "sentences"
})
@NoArgsConstructor
@Getter
public class Chunk {

    @JsonProperty("sentences")
    public List<Sentence> sentences;

}
