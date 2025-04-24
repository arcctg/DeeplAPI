package org.arcctg.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "jobs",
    "texts",
    "lang",
    "priority",
    "commonJobParams",
    "timestamp"
})
@Builder
public class Params {

    @JsonProperty("jobs")
    public List<Job> jobs;
    @JsonProperty("lang")
    public Lang lang;
    @JsonProperty("priority")
    public Integer priority;
    @JsonProperty("texts")
    public List<String> texts;
    @JsonProperty("commonJobParams")
    public CommonJobParams commonJobParams;
    @JsonProperty("timestamp")
    public Long timestamp;

}
