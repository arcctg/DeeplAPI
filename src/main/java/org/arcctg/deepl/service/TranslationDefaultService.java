package org.arcctg.deepl.service;

import static org.arcctg.deepl.request.PayloadBuilder.buildForAllSentences;
import static org.arcctg.deepl.request.RequestBuilder.buildDefault;
import static org.arcctg.deepl.response.ResponseParser.parseTextTranslation;
import static org.arcctg.utils.Utility.sendRequest;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.arcctg.deepl.model.SourceTargetLangs;
import org.arcctg.deepl.service.interfaces.SegmentationService;
import org.arcctg.deepl.service.interfaces.TranslationService;
import org.arcctg.model.common.Sentence;

public class TranslationDefaultService implements TranslationService {
    private final SegmentationService segmentationService;

    public TranslationDefaultService() {
        this.segmentationService = new SegmentationServiceClass();
    }

    @Override
    public String process(String text, SourceTargetLangs langPair) {
        List<Sentence> allSentences = segmentationService.process(text);

        return translateSentences(allSentences, langPair);
    }

    private String translateSentences(List<Sentence> sentences, SourceTargetLangs langPair) {
        StringBuilder result = new StringBuilder();
        List<String> payloads = buildForAllSentences(sentences, langPair);

        for (String payload : payloads) {
            HttpRequest request = buildDefault(payload);
            HttpResponse<String> response = sendRequest(request);
            String parsedResponse = parseTextTranslation(response.body());

            result.append(parsedResponse);
        }

        return result.toString().trim();
    }
}
