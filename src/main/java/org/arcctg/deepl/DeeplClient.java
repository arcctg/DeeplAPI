package org.arcctg.deepl;

import static org.arcctg.deepl.RequestBuilder.buildDefault;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import lombok.SneakyThrows;
import org.arcctg.json.Sentence;

public class DeeplClient {
    private final HttpClient client;
    private final PayloadBuilder payloadBuilder;
    private final ResponseParser responseParser;

    public DeeplClient() {
        this.client = HttpClient.newBuilder().build();
        this.payloadBuilder = new PayloadBuilder();
        this.responseParser = new ResponseParser();
    }

    public String getAlternativesAtPosition(int position) {
        return "";
    }

    public String translate(String text, SourceTargetLangs langPair) {
        List<Sentence> allSentences = getSentencesFromText(text);

        return translateSentences(allSentences, langPair);
    }

    private String translateSentences(List<Sentence> sentences, SourceTargetLangs langPair) {
        StringBuilder result = new StringBuilder();
        List<String> payloads = payloadBuilder.buildForAllSentences(sentences, langPair);

        for (String payload : payloads) {
            HttpRequest request = buildDefault(payload);
            HttpResponse<String> response = sendRequest(request);
            String parsedResponse = responseParser.parseTextTranslation(response.body());

            result.append(parsedResponse);
        }

        return result.toString().trim();
    }

    private List<Sentence> getSentencesFromText(String text) {
        String jsonResponse = requestTextSegmentation(text);

        return responseParser.parseTextSegmentation(jsonResponse);
    }

    private String requestTextSegmentation(String text) {
        String payload = payloadBuilder.buildForTextSegmentation(text);
        HttpRequest request = buildDefault(payload);
        HttpResponse<String> response = sendRequest(request);

        return response.body();
    }

    @SneakyThrows
    private HttpResponse<String> sendRequest(HttpRequest request) {
        return client.send(request, BodyHandlers.ofString());
    }
}
