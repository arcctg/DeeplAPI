package org.arcctg.deepl;

import static org.arcctg.utils.Utility.generateId;
import static org.arcctg.utils.Utility.generateTimestamp;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.SneakyThrows;
import org.arcctg.json.*;

public class DeeplClient {
    private static final String API_URL = "https://www2.deepl.com/jsonrpc";
    private final HttpClient client;
    private final ObjectMapper objectMapper;
    private Long id;

    public DeeplClient() {
        this.client = HttpClient.newBuilder().build();
        this.objectMapper = new ObjectMapper();
        this.id = generateId();
    }

    public String getAlternativesAtPosition(int position) {
        return "";
    }

    @SneakyThrows
    public String translate(String text, SourceTargetLangs sourceTargetLangs) {
        List<Sentence> allSentences = splitText(text);
        List<Job> allJobs = buildJobs(allSentences);
        StringBuilder result = new StringBuilder();

        List<String> translationPayloads = buildPayloadForEachJobBatch(allJobs, sourceTargetLangs);

        for (String payload : translationPayloads) {
            HttpRequest request = buildRequest(payload);
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String parsedResponse = parseTranslationResponse(response.body());
            result.append(parsedResponse);
        }

        return result.toString();
    }

    @SneakyThrows
    private List<Sentence> splitText(String text) {
        String jsonResponse = sendSplitTextRequest(text);

        ResponseTemplate response = objectMapper.readValue(jsonResponse, ResponseTemplate.class);

        List<Sentence> sentences = new ArrayList<>();
        long idCounter = 1;

        for (Text textBlock : response.getResult().getTexts()) {
            for (Chunk chunk : textBlock.getChunks()) {
                for (Sentence sentence : chunk.getSentences()) {
                    sentence.setId(idCounter++);
                    sentences.add(sentence);

                    idCounter = idCounter >= 99 ? 1 : idCounter;
                }
            }
        }

        return sentences;
    }

    @SneakyThrows
    private String sendSplitTextRequest(String text) {
        String payload = buildSplitTextPayload(text);

        HttpRequest request = buildRequest(payload);

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    @SneakyThrows
    private String buildSplitTextPayload(String text) {
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

    private String modifyPayloadForDeepl(String payload) {
        String replacement = ((id + 3) % 13 == 0 || (id + 5) % 29 == 0) ? "method\" : " : "method\": ";

        return payload.replace("method\":", replacement);
    }

    private List<Job> buildJobs(List<Sentence> sentences) {
        List<Job> jobs = new ArrayList<>();

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

            jobs.add(Job.builder()
                .kind("default")
                .sentences(Collections.singletonList(sentence))
                .rawEnContextBefore(rawEnContextBefore)
                .rawEnContextAfter(rawEnContextAfter)
                .preferredNumBeams(1)
                .build());
        }

        return jobs;
    }

    @SneakyThrows
    private List<String> buildPayloadForEachJobBatch(List<Job> allJobs,
        SourceTargetLangs sourceTargetLangs) {
        List<String> payloads = new ArrayList<>();

        for (int i = 0; i < allJobs.size(); i += 13) {
            List<Job> batch = allJobs.subList(i, Math.min(i + 13, allJobs.size()));
            List<String> batchSentences = extractJobsText(batch);
            String payload = buildTranslationPayload(batch, sourceTargetLangs, batchSentences);

            payloads.add(payload);
        }

        return payloads;
    }

    private List<String> extractJobsText(List<Job> batch) {
        return batch.stream()
            .flatMap(job -> job.getSentences().stream())
            .map(Sentence::getText)
            .toList();
    }

    @SneakyThrows
    private String buildTranslationPayload(List<Job> jobs, SourceTargetLangs sourceTargetLangs,
        List<String> stringSentences) {
        Preference preference = Preference.builder()
            .weight(new Weight())
            ._default("default")
            .build();

        Lang lang = Lang.builder()
            .targetLang(sourceTargetLangs.getTargetLang())
            .sourceLangComputed(sourceTargetLangs.getSourceLang())
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
            .timestamp(generateTimestamp(stringSentences))
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

    @SneakyThrows
    private HttpRequest buildRequest(String payload) {
        return HttpRequest.newBuilder()
            .uri(new URI(API_URL))
            .header("accept", "*/*")
            .header("accept-language", "en-UA,en;q=0.9")
            .header("content-type", "application/json")
            .header("dnt", "1")
            .header("origin", "https://www.deepl.com")
            .header("priority", "u=1, i")
            .header("referer", "https://www.deepl.com/")
            .header("sec-ch-ua",
                "Not(A:Brand\";v=\"99\", \"Google Chrome\";v=\"133\", \"Chromium\";v=\"133\"")
            .header("sec-ch-ua-mobile", "?0")
            .header("sec-ch-ua-platform", "\"Windows\"")
            .header("sec-fetch-dest", "empty")
            .header("sec-fetch-mode", "cors")
            .header("sec-fetch-site", "same-site")
            .header("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) "
                    + "Chrome/133.0.0.0 Safari/537.36")
            .POST(HttpRequest.BodyPublishers.ofString(payload))
            .build();
    }

    @SneakyThrows
    private String parseTranslationResponse(String jsonResponse) {
        ResponseTemplate response = objectMapper.readValue(jsonResponse, ResponseTemplate.class);
        StringBuilder stringBuilder = new StringBuilder();

        for (Translation translation : response.getResult().getTranslations()) {
            for (Beam beam : translation.getBeams()) {
                for (Sentence sentence : beam.getSentences()) {
                    stringBuilder.append(" ").append(new String(sentence.getText().getBytes(),
                        StandardCharsets.UTF_8));
                }
            }
        }

        return stringBuilder.toString();
    }
}
