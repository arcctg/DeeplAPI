package org.arcctg.service.impl;

import static org.arcctg.util.Utility.generateTimestamp;
import static org.arcctg.util.Utility.getIdGenerator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Singleton;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.SneakyThrows;
import org.arcctg.deepl.model.SourceTargetLangs;
import org.arcctg.deepl.model.dto.common.Sentence;
import org.arcctg.deepl.model.dto.request.Job;
import org.arcctg.deepl.model.dto.request.PayloadTemplate;
import org.arcctg.deepl.model.dto.request.Weight;
import org.arcctg.service.api.PayloadBuilderService;
import org.arcctg.util.IdGenerator;

@Singleton
public class DefaultPayloadBuilderService implements PayloadBuilderService {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final IdGenerator id = getIdGenerator();

    @Override
    @SneakyThrows
    public String buildForTextSegmentation(String text) {
        List<String> texts = Arrays.stream(text.split("\n+")).map(String::trim).toList();

        PayloadTemplate payloadTemplate = PayloadTemplate.builder()
            .jsonrpc("2.0")
            .method("LMT_split_text")
            .id(id.next())
            .params(paramsBuilder -> paramsBuilder
                .texts(texts)
                .lang(langBuilder -> langBuilder
                    .langUserSelected("EN")
                    .preference(preferenceBuilder -> preferenceBuilder
                        .weight(new Weight())
                        ._default("default")
                    )
                )
                .commonJobParams(commonJobParamsBuilder -> commonJobParamsBuilder
                    .mode("translate")
                    .textType("plaintext")
                )
            )
            .build();

        String payload = objectMapper.writeValueAsString(payloadTemplate);

        return modifyPayloadForDeepl(payload);
    }

    @Override
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

    @SneakyThrows
    public static String buildForTranslation(List<Job> jobs, SourceTargetLangs langPair,
        List<String> batchText) {

        PayloadTemplate payloadTemplate = PayloadTemplate.builder()
            .jsonrpc("2.0")
            .method("LMT_handle_jobs")
            .id(id.next())
            .params(paramsBuilder -> paramsBuilder
                .jobs(jobs)
                .priority(1)
                .timestamp(generateTimestamp(batchText))
                .lang(langBuilder -> langBuilder
                    .targetLang(langPair.getTargetLang())
                    .sourceLangComputed(langPair.getSourceLang())
                    .preference(preferenceBuilder -> preferenceBuilder
                        .weight(new Weight())
                        ._default("default")
                    )
                )
                .commonJobParams(commonJobParamsBuilder -> commonJobParamsBuilder
                    .quality("normal")
                    .mode("translate")
                    .browserType(1)
                    .textType("plaintext")
                )
            )
            .build();

        String payload = objectMapper.writeValueAsString(payloadTemplate);

        return modifyPayloadForDeepl(payload);
    }

    private static String modifyPayloadForDeepl(String payload) {
        String replacement = ((id.get() + 3) % 13 == 0 || (id.get() + 5) % 29 == 0) ? "method\" : " : "method\": ";

        return payload.replace("method\":", replacement);
    }

    private static List<List<Job>> buildJobsBatches(List<Sentence> allSentences) {
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

    private static Job createJobForSentence(int sentenceIndex, List<Sentence> allSentences) {
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

    private static List<String> createContextAfter(int sentenceIndex, List<Sentence> allSentences) {
        List<String> rawEnContextAfter = new ArrayList<>();

        if (sentenceIndex != allSentences.size() - 1) {
            rawEnContextAfter.add(allSentences.get(sentenceIndex + 1).getText());
        }

        return rawEnContextAfter;
    }

    private static List<String> createContextBefore(int sentenceIndex, List<Sentence> allSentences) {
        List<String> rawEnContextBefore = new ArrayList<>();
        int j = sentenceIndex >= 5 ? sentenceIndex - 5 : 0;

        while (j != sentenceIndex && rawEnContextBefore.size() != 5) {
            rawEnContextBefore.add(allSentences.get(j++).getText());
        }

        return rawEnContextBefore;
    }

    @SneakyThrows
    private static int getBytesLength(List<Job> jobBatch, Job job) {
        String batchStr = objectMapper.writeValueAsString(jobBatch);
        String jobStr = objectMapper.writeValueAsString(job);

        return (batchStr + jobStr).getBytes(StandardCharsets.UTF_8).length;
    }

    private static List<String> extractBatchText(List<Job> batch) {
        return batch.stream()
            .flatMap(job -> job.getSentences().stream())
            .map(Sentence::getText)
            .toList();
    }
}
