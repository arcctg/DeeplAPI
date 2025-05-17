package org.arcctg.deepl.model.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.function.Consumer;
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
    public Integer id;

    public static class PayloadTemplateBuilder {
        public PayloadTemplateBuilder params(Consumer<Params.ParamsBuilder> consumer) {
            Params.ParamsBuilder builder = Params.builder();
            consumer.accept(builder);
            this.params = builder.build();

            return this;
        }
    }
}
