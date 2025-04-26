package org.arcctg.deepl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.SneakyThrows;
import org.arcctg.json.Job;
import org.arcctg.json.Sentence;

public class DeeplClient {
    private static final String API_URL = "https://www2.deepl.com/jsonrpc";
    private final HttpClient client;
    private Long id = 100_000L;

    public DeeplClient() {
        this.client = HttpClient.newBuilder().build();
    }

    public String getAlternativesAtPosition(int position) {
        return "";
    }

    @SneakyThrows
    public String translate(String text, SourceTargetLangs sourceTargetLangs) {
        List<Sentence> allSentences = splitText(text);
        StringBuilder result = new StringBuilder();

        List<String> payloads = buildHandleJobsPayloads(allSentences, sourceTargetLangs);

        return result.toString();
    }

    private List<Sentence> splitText(String text) {
        List<Sentence> sentences = new ArrayList<>();
        long idCounter = 1;

        for (String string : text.split("\n+")) {
            sentences.add(new Sentence(string, idCounter++, ""));
        }

        return sentences;
    }

    @SneakyThrows
    private List<String> buildHandleJobsPayloads(List<Sentence> sentences,
        SourceTargetLangs sourceTargetLangs) {
        List<String> payloads = new ArrayList<>();
        List<Job> allJobs = buildJobs(sentences);

        return payloads;
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
    private static HttpRequest buildRequest(String payload) {
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
}
