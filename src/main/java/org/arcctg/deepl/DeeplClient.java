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


    @SneakyThrows
    public String translate(String text, SourceTargetLangs sourceTargetLangs) {
        List<Sentence> allSentences = splitText(text);
        StringBuilder result = new StringBuilder();

        List<String> payloads = payloadBuilder.buildForAllSentences(allSentences, sourceTargetLangs);

        for (String payload : payloads) {
            HttpRequest request = buildDefault(payload);
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            String parsedResponse = responseParser.parseTextTranslation(response.body());

            result.append(parsedResponse);
        }

        return result.toString();
    }

    @SneakyThrows
    private List<Sentence> splitText(String text) {
        String jsonResponse = sendSplitTextRequest(text);

        return responseParser.parseTextSplitting(jsonResponse);
    }

    @SneakyThrows
    private String sendSplitTextRequest(String text) {
        String payload = payloadBuilder.buildForTextSplitting(text);

        HttpRequest request = buildDefault(payload);

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

        return response.body();
    }
}
