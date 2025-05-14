package org.arcctg.deepl.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "jsonrpc",
    "id",
    "result"
})
@NoArgsConstructor
@Getter
public class ResponseTemplate {

    @JsonProperty("jsonrpc")
    public String jsonrpc;
    @JsonProperty("id")
    public long id;
    @JsonProperty("result")
    public Result result;

}
