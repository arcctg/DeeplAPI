package org.arcctg.deepl;

import static org.arcctg.utils.Utility.generateId;
import static org.arcctg.utils.Utility.generateTimestamp;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.SneakyThrows;
import org.arcctg.json.CommonJobParams;
import org.arcctg.json.Job;
import org.arcctg.json.Lang;
import org.arcctg.json.Params;
import org.arcctg.json.PayloadTemplate;
import org.arcctg.json.Preference;
import org.arcctg.json.Sentence;
import org.arcctg.json.Weight;

public class PayloadBuilder {
    private final ObjectMapper objectMapper;
    private Long id;

    public PayloadBuilder() {
        this.objectMapper = new ObjectMapper();
        this.id = generateId();
    }

    @SneakyThrows
    public String buildForTextSegmentation(String text) {
        List<String> texts = Arrays.stream(text.split("\n+")).map(String::trim).toList();

        CommonJobParams commonJobParams = CommonJobParams.builder()
            .mode("translate")
            .textType("plaintext")
            .build();

        Preference preference = Preference.builder()
            .weight(new Weight())
            ._default("default")
            .build();

        Lang lang = Lang.builder()
            .langUserSelected("EN")
            .preference(preference)
            .build();

        Params params = Params.builder()
            .texts(texts)
            .commonJobParams(commonJobParams)
            .lang(lang)
            .build();

        PayloadTemplate payloadTemplate = PayloadTemplate.builder()
            .jsonrpc("2.0")
            .method("LMT_split_text")
            .params(params)
            .id(++id)
            .build();

        String payload = objectMapper.writeValueAsString(payloadTemplate);

        return modifyPayloadForDeepl(payload);
    }

    @SneakyThrows
    public String buildForTranslation(List<Job> jobs, SourceTargetLangs langPair,
        List<String> batchText) {
        Preference preference = Preference.builder()
            .weight(new Weight())
            ._default("default")
            .build();

        Lang lang = Lang.builder()
            .targetLang(langPair.getTargetLang())
            .sourceLangComputed(langPair.getSourceLang())
            .preference(preference)
            .build();

        CommonJobParams commonJobParams = CommonJobParams.builder()
            .quality("normal")
            .mode("translate")
            .browserType(1)
            .textType("plaintext")
            .build();

        Params params = Params.builder()
            .jobs(jobs)
            .lang(lang)
            .commonJobParams(commonJobParams)
            .priority(1)
            .timestamp(generateTimestamp(batchText))
            .build();

        PayloadTemplate payloadTemplate = PayloadTemplate.builder()
            .jsonrpc("2.0")
            .method("LMT_handle_jobs")
            .params(params)
            .id(++id)
            .build();

        String payload = objectMapper.writeValueAsString(payloadTemplate);

        return modifyPayloadForDeepl(payload);
    }

    private String modifyPayloadForDeepl(String payload) {
        String replacement = ((id + 3) % 13 == 0 || (id + 5) % 29 == 0) ? "method\" : " : "method\": ";

        return payload.replace("method\":", replacement);
    }

    @SneakyThrows
    public List<String> buildForAllSentences(List<Sentence> allSentences,
        SourceTargetLangs langPair) {
        List<String> payloads = new ArrayList<>();

        for (List<Job> batch : buildJobsBatches(allSentences)) {
            List<String> batchText = extractBatchText(batch);
            String payload = buildForTranslation(batch, langPair, batchText);

            payloads.add(payload);
        }

        return payloads;
    }

    private List<List<Job>> buildJobsBatches(List<Sentence> sentences) {
        List<List<Job>> jobBatchesList = new ArrayList<>();
        List<Job> jobBatch = new ArrayList<>();

        for (int i = 0; i < sentences.size(); i++) {
            Sentence sentence = sentences.get(i);

            List<String> rawEnContextAfter = new ArrayList<>();
            if (i != sentences.size() - 1) {
                rawEnContextAfter.add(sentences.get(i + 1).getText());
            }

            List<String> rawEnContextBefore = new ArrayList<>();
            int j = i >= 5 ? i - 5 : 0;

            while (j != i && rawEnContextBefore.size() != 5) {
                rawEnContextBefore.add(sentences.get(j++).getText());
            }

            Job job = Job.builder()
                .kind("default")
                .sentences(Collections.singletonList(sentence))
                .rawEnContextBefore(rawEnContextBefore)
                .rawEnContextAfter(rawEnContextAfter)
                .preferredNumBeams(1)
                .build();

            jobBatch.add(job);

            if (jobBatch.size() == 13 || i == sentences.size() - 1) {
                jobBatchesList.add(jobBatch);
                jobBatch = new ArrayList<>();
            }
        }

        return jobBatchesList;
    }

    private List<String> extractBatchText(List<Job> batch) {
        return batch.stream()
            .flatMap(job -> job.getSentences().stream())
            .map(Sentence::getText)
            .toList();
    }
}
