package org.arcctg.service.impl;

import static org.arcctg.deepl.builder.PayloadBuilder.buildForAllSentences;
import static org.arcctg.deepl.parser.ResponseParser.parseTextTranslation;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Queue;

import org.arcctg.deepl.model.SourceTargetLangs;
import org.arcctg.service.api.QueueRequestService;
import org.arcctg.service.api.SegmentationService;
import org.arcctg.service.api.TranslationService;
import org.arcctg.deepl.model.dto.common.Sentence;
import org.arcctg.util.handler.api.RequestHandler;
import org.arcctg.util.handler.impl.DefaultRequestHandler;

public class TranslationSyncService implements TranslationService {
    private final SegmentationService segmentationService;
    private final QueueRequestService queueRequestService;
    private final RequestHandler requestHandler;

    public TranslationSyncService() {
        this(new DefaultRequestHandler());
    }

    public TranslationSyncService(RequestHandler requestHandler) {
        this.segmentationService = new DefaultSegmentationService();
        this.queueRequestService = new AsyncQueueRequestService();
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
        Queue<HttpRequest> requestQueue = queueRequestService.process(payloads);

        while (!requestQueue.isEmpty()) {
            HttpRequest request = requestQueue.poll();
            HttpResponse<String> response = requestHandler.sendRequest(request);
            String translatedText = parseTextTranslation(response.body());

            result.append(translatedText);
        }

        return result.toString().trim();
    }
}

//private String translateSentences(List<Sentence> sentences, SourceTargetLangs langPair) {
//    List<String> payloads = buildForAllSentences(sentences, langPair);
//    Queue<HttpRequest> requests = queueRequestService.process(payloads);
//
//    return requests.stream()
//            .map(requestHandler::sendRequest)
//            .map(HttpResponse::body)
//            .map(ResponseParser::parseTextTranslation)
//            .collect(Collectors.joining());
//}
