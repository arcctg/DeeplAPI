package org.arcctg.service.impl;

import static org.arcctg.util.Utility.generateTimestamp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.arcctg.deepl.model.SourceTargetLangs;
import org.arcctg.deepl.model.dto.common.Sentence;
import org.arcctg.deepl.model.dto.request.Job;
import org.arcctg.deepl.model.dto.request.PayloadTemplate;
import org.arcctg.deepl.model.dto.request.Weight;
import org.arcctg.service.api.PayloadBuilderService;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class DefaultPayloadBuilderService implements PayloadBuilderService {

    private static final String JSON_RPC_VERSION = "2.0";
    private static final String DEEPL_METHOD_SPLIT_TEXT = "LMT_split_text";
    private static final String DEEPL_METHOD_HANDLE_JOBS = "LMT_handle_jobs";
    private static final String DEFAULT_SOURCE_LANG = "EN";
    private static final String DEFAULT_LANG_PREFERENCE = "default";
    private static final String MODE_TRANSLATE = "translate";
    private static final String INPUT_TEXT_TYPE = "plaintext";
    private static final String TRANSLATION_QUALITY = "normal";
    private static final String JOB_KIND = "default";
    private static final String METHOD_JSON_FORMAT_DEFAULT = "method\": ";
    private static final String METHOD_JSON_FORMAT_ALTERNATIVE = "method\" : ";
    private static final String METHOD_JSON_FORMAT_ORIGINAL = "method\":";

    private static final int MAX_BATCH_SIZE_BYTES = 32_000;
    private static final int MAX_CONTEXT_SENTENCES_BEFORE = 5;
    private static final int DEFAULT_TRANSLATION_PRIORITY = 1;
    private static final int DEFAULT_BROWSER_TYPE = 1;
    private static final int DEFAULT_NUM_BEAMS = 1;
    private static final int DIVISOR_FIRST = 13;
    private static final int REMAINDER_FIRST = 3;
    private static final int DIVISOR_SECOND = 29;
    private static final int REMAINDER_SECOND = 5;

    private final ObjectMapper objectMapper;
    @Named("Payload id")
    private final AtomicInteger id;

    @Override
    @SneakyThrows
    public String buildForTextSegmentation(String text) {
        List<String> texts = Arrays.stream(text.split("\n+")).map(String::trim).toList();

        PayloadTemplate payloadTemplate = PayloadTemplate.builder()
            .jsonrpc(JSON_RPC_VERSION)
            .method(DEEPL_METHOD_SPLIT_TEXT)
            .id(id.incrementAndGet())
            .params(paramsBuilder -> paramsBuilder
                .texts(texts)
                .lang(langBuilder -> langBuilder
                    .langUserSelected(DEFAULT_SOURCE_LANG)
                    .preference(preferenceBuilder -> preferenceBuilder
                        .weight(new Weight())
                        ._default(DEFAULT_LANG_PREFERENCE)
                    )
                )
                .commonJobParams(commonJobParamsBuilder -> commonJobParamsBuilder
                    .mode(MODE_TRANSLATE)
                    .textType(INPUT_TEXT_TYPE)
                )
            )
            .build();

        String payload = objectMapper.writeValueAsString(payloadTemplate);

        return modifyPayloadForDeepl(payload);
    }

    @SneakyThrows
    public String buildForTranslation(List<Job> jobs, SourceTargetLangs langPair) {
        List<String> batchText = extractBatchText(jobs);

        PayloadTemplate payloadTemplate = PayloadTemplate.builder()
            .jsonrpc(JSON_RPC_VERSION)
            .method(DEEPL_METHOD_HANDLE_JOBS)
            .id(id.incrementAndGet())
            .params(paramsBuilder -> paramsBuilder
                .jobs(jobs)
                .priority(DEFAULT_TRANSLATION_PRIORITY)
                .timestamp(generateTimestamp(batchText))
                .lang(langBuilder -> langBuilder
                    .targetLang(langPair.getTargetLang())
                    .sourceLangComputed(langPair.getSourceLang())
                    .preference(preferenceBuilder -> preferenceBuilder
                        .weight(new Weight())
                        ._default(DEFAULT_LANG_PREFERENCE)
                    )
                )
                .commonJobParams(commonJobParamsBuilder -> commonJobParamsBuilder
                    .quality(TRANSLATION_QUALITY)
                    .mode(MODE_TRANSLATE)
                    .browserType(DEFAULT_BROWSER_TYPE)
                    .textType(INPUT_TEXT_TYPE)
                )
            )
            .build();

        String payload = objectMapper.writeValueAsString(payloadTemplate);

        return modifyPayloadForDeepl(payload);
    }

    private String modifyPayloadForDeepl(String payload) {
        String replacement = ((id.get() + REMAINDER_FIRST) % DIVISOR_FIRST == 0
            || (id.get() + REMAINDER_SECOND) % DIVISOR_SECOND == 0)
            ? METHOD_JSON_FORMAT_ALTERNATIVE
            : METHOD_JSON_FORMAT_DEFAULT;

        return payload.replace(METHOD_JSON_FORMAT_ORIGINAL, replacement);
    }

    @Override
    public List<String> buildForAllSentences(List<Sentence> allSentences,
        SourceTargetLangs langPair) {

        return buildJobsBatches(allSentences).parallelStream()
            .map(batch -> buildForTranslation(batch, langPair))
            .toList();
    }

    private List<List<Job>> buildJobsBatches(List<Sentence> allSentences) {
        List<List<Job>> jobBatchesList = new ArrayList<>();
        List<Job> currentBatch = new ArrayList<>();
        List<Job> allJobs = createAllJobs(allSentences);

        for (Job job : allJobs) {
            if (isBatchFull(currentBatch, job)) {
                jobBatchesList.add(new ArrayList<>(currentBatch));
                currentBatch.clear();
            }
            currentBatch.add(job);
        }

        if (!currentBatch.isEmpty()) {
            jobBatchesList.add(currentBatch);
        }

        return jobBatchesList;
    }

    private List<Job> createAllJobs(List<Sentence> allSentences) {
        return IntStream.range(0, allSentences.size())
            .parallel()
            .mapToObj(i -> createJobForSentence(i, allSentences))
            .toList();
    }

    private boolean isBatchFull(List<Job> currentBatch, Job job) {
        return !currentBatch.isEmpty() && getBytesLength(currentBatch, job) > MAX_BATCH_SIZE_BYTES;
    }

    private Job createJobForSentence(int sentenceIndex, List<Sentence> allSentences) {
        List<String> rawEnContextAfter = createContextAfter(sentenceIndex, allSentences);
        List<String> rawEnContextBefore = createContextBefore(sentenceIndex, allSentences);

        return Job.builder()
            .kind(JOB_KIND)
            .sentences(Collections.singletonList(allSentences.get(sentenceIndex)))
            .rawEnContextBefore(rawEnContextBefore)
            .rawEnContextAfter(rawEnContextAfter)
            .preferredNumBeams(DEFAULT_NUM_BEAMS)
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
        int j = sentenceIndex >= MAX_CONTEXT_SENTENCES_BEFORE
            ? sentenceIndex - MAX_CONTEXT_SENTENCES_BEFORE
            : 0;

        while (j != sentenceIndex && rawEnContextBefore.size() != MAX_CONTEXT_SENTENCES_BEFORE) {
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