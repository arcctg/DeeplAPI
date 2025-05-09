package org.arcctg.deepl.request;

import static org.arcctg.utils.Utility.getIdGenerator;
import static org.arcctg.utils.Utility.generateTimestamp;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.SneakyThrows;
import org.arcctg.deepl.model.SourceTargetLangs;
import org.arcctg.model.request.*;
import org.arcctg.model.common.Sentence;
import org.arcctg.utils.IdGenerator;

public class PayloadBuilder {
    private final ObjectMapper objectMapper;
    private final IdGenerator id;

    public PayloadBuilder() {
        this.objectMapper = new ObjectMapper();
        this.id = getIdGenerator();
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
            .id(id.next())
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
            .id(id.next())
            .build();

        String payload = objectMapper.writeValueAsString(payloadTemplate);

        return modifyPayloadForDeepl(payload);
    }

    private String modifyPayloadForDeepl(String payload) {
        String replacement = ((id.get() + 3) % 13 == 0 || (id.get() + 5) % 29 == 0) ? "method\" : " : "method\": ";

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

    private List<List<Job>> buildJobsBatches(List<Sentence> allSentences) {
        List<List<Job>> jobBatchesList = new ArrayList<>();
        List<Job> jobBatch = new ArrayList<>();

        for (int i = 0; i < allSentences.size(); i++) {
            Job job = createJobForSentence(i, allSentences);

            if (getBytesLength(jobBatch, job) > 32_000) {
                jobBatchesList.add(jobBatch);
                jobBatch = new ArrayList<>();
            }

            jobBatch.add(job);
        }

        if (!jobBatch.isEmpty()) {
            jobBatchesList.add(jobBatch);
        }

        return jobBatchesList;
    }

    private Job createJobForSentence(int sentenceIndex, List<Sentence> allSentences) {
        List<String> rawEnContextAfter = createContextAfter(sentenceIndex, allSentences);
        List<String> rawEnContextBefore = createContextBefore(sentenceIndex, allSentences);

        return Job.builder()
            .kind("default")
            .sentences(Collections.singletonList(allSentences.get(sentenceIndex)))
            .rawEnContextBefore(rawEnContextBefore)
            .rawEnContextAfter(rawEnContextAfter)
            .preferredNumBeams(1)
            .build();
    }

    private List<String> createContextAfter(int sentenceIndex, List<Sentence> allSentences) {
        List<String> rawEnContextAfter = new ArrayList<>();

        if (sentenceIndex != allSentences.size() - 1) {
            rawEnContextAfter.add(allSentences.get(sentenceIndex + 1).getText());
        }

        return rawEnContextAfter;
    }

    private List<String> createContextBefore(int sentenceIndex, List<Sentence> allSentences) {
        List<String> rawEnContextBefore = new ArrayList<>();
        int j = sentenceIndex >= 5 ? sentenceIndex - 5 : 0;

        while (j != sentenceIndex && rawEnContextBefore.size() != 5) {
            rawEnContextBefore.add(allSentences.get(j++).getText());
        }

        return rawEnContextBefore;
    }

    @SneakyThrows
    private int getBytesLength(List<Job> jobBatch, Job job) {
        String batchStr = objectMapper.writeValueAsString(jobBatch);
        String jobStr = objectMapper.writeValueAsString(job);

        return (batchStr + jobStr).getBytes(StandardCharsets.UTF_8).length;
    }

    private List<String> extractBatchText(List<Job> batch) {
        return batch.stream()
            .flatMap(job -> job.getSentences().stream())
            .map(Sentence::getText)
            .toList();
    }
}
