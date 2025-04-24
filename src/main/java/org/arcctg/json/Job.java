package org.arcctg.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "kind",
    "sentences",
    "raw_en_context_before",
    "raw_en_context_after",
    "preferred_num_beams",
    "write_variant_requests"
})
@Builder
public class Job {

    @JsonProperty("kind")
    private String kind;
    @JsonProperty("sentences")
    private List<Sentence> sentences;
    @JsonProperty("raw_en_context_before")
    private List<String> rawEnContextBefore;
    @JsonProperty("raw_en_context_after")
    private List<String> rawEnContextAfter;
    @JsonProperty("preferred_num_beams")
    private Integer preferredNumBeams;
    @JsonProperty("write_variant_requests")
    public List<String> writeVariantRequests;

}
