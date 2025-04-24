package org.arcctg.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "jsonrpc",
    "method",
    "params",
    "id"
})
@Builder
public class PayloadTemplate {

    @JsonProperty("jsonrpc")
    public String jsonrpc;
    @JsonProperty("method")
    public String method;
    @JsonProperty("params")
    public Params params;
    @JsonProperty("id")
    public Long id;

}
