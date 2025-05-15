package org.arcctg.service.impl;

import static org.arcctg.deepl.builder.PayloadBuilder.buildForAllSentences;
import static org.arcctg.deepl.builder.RequestBuilder.buildDefault;
import static org.arcctg.deepl.parser.ResponseParser.parseTextTranslation;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.arcctg.deepl.model.SourceTargetLangs;
import org.arcctg.service.api.SegmentationService;
import org.arcctg.service.api.TranslationService;
import org.arcctg.deepl.model.dto.common.Sentence;
import org.arcctg.util.handler.api.RequestHandler;
import org.arcctg.util.handler.impl.DefaultRequestHandler;

public class TranslationSyncService implements TranslationService {
    private final SegmentationService segmentationService;
    private final RequestHandler requestHandler;

    public TranslationSyncService() {
        this(new DefaultRequestHandler());
    }

    public TranslationSyncService(RequestHandler requestHandler) {
        this.segmentationService = new SegmentationServiceImpl();
        this.requestHandler = requestHandler;
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
            HttpResponse<String> response = requestHandler.sendRequest(request);
            String parsedResponse = parseTextTranslation(response.body());

            result.append(parsedResponse);
        }

        return result.toString().trim();
    }
}
