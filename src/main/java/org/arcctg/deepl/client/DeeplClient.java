package org.arcctg.deepl.client;

import static org.arcctg.deepl.request.RequestBuilder.buildDefault;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import lombok.SneakyThrows;
import org.arcctg.cache.translation.TranslationCacheService;
import org.arcctg.deepl.request.PayloadBuilder;
import org.arcctg.deepl.response.ResponseParser;
import org.arcctg.deepl.model.SourceTargetLangs;
import org.arcctg.model.common.Sentence;

public class DeeplClient {
    private final HttpClient client;
    private final PayloadBuilder payloadBuilder;
    private final ResponseParser responseParser;
    private final TranslationCacheService cacheService;

    public DeeplClient() {
        this(new TranslationCacheService());
    }

    public DeeplClient(TranslationCacheService cacheService) {
        this.client = HttpClient.newBuilder().build();
        this.payloadBuilder = new PayloadBuilder();
        this.responseParser = new ResponseParser();
        this.cacheService = cacheService;
    }


    public String getAlternativesAtPosition(int position) {
        return "";
    }

    public String translate(String text, SourceTargetLangs langPair) {
        String cachedTranslation = cacheService.getCachedTranslation(text, langPair);
        if (cachedTranslation != null) {
            System.out.println("Using cached translation for text: " + text);
            return cachedTranslation;
        }

        List<Sentence> allSentences = getSentencesFromText(text);
        String translation = translateSentences(allSentences, langPair);

        cacheService.cacheTranslation(text, langPair, translation);

        return translation;
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

    public void clearCaches() {
        cacheService.clearCaches();
    }

    public int getTranslationCacheSize() {
        return cacheService.getTranslationCacheSize();
    }
}
