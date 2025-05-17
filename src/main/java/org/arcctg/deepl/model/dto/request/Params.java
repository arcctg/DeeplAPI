package org.arcctg.deepl.model.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import java.util.function.Consumer;

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

    public static class ParamsBuilder {
        public ParamsBuilder lang(Consumer<Lang.LangBuilder> consumer) {
            Lang.LangBuilder builder = Lang.builder();
            consumer.accept(builder);
            this.lang = builder.build();

            return this;
        }

        public ParamsBuilder commonJobParams(Consumer<CommonJobParams.CommonJobParamsBuilder> consumer) {
            CommonJobParams.CommonJobParamsBuilder builder = CommonJobParams.builder();
            consumer.accept(builder);
            this.commonJobParams = builder.build();

            return this;
        }
    }
}
