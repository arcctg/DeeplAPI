package org.arcctg.service.impl;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Queue;
import lombok.RequiredArgsConstructor;
import org.arcctg.deepl.model.SourceTargetLangs;
import org.arcctg.deepl.model.dto.common.Sentence;
import org.arcctg.service.api.PayloadBuilderService;
import org.arcctg.service.api.QueueRequestService;
import org.arcctg.service.api.RequestHandlerService;
import org.arcctg.service.api.ResponseParserService;
import org.arcctg.service.api.SegmentationService;
import org.arcctg.service.api.TranslationService;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class TranslationSyncService implements TranslationService {

    private final @Assisted RequestHandlerService requestHandler;
    private final SegmentationService segmentationService;
    private final QueueRequestService queueRequestService;
    private final PayloadBuilderService payloadBuilderService;
    private final ResponseParserService responseParser;

    @Override
    public String process(String text, SourceTargetLangs langPair) {
        List<Sentence> allSentences = segmentationService.process(text);

        return translateSentences(allSentences, langPair);
    }

    private String translateSentences(List<Sentence> sentences, SourceTargetLangs langPair) {
        StringBuilder result = new StringBuilder();
        List<String> payloads = payloadBuilderService.buildForAllSentences(sentences, langPair);
        Queue<HttpRequest> requestQueue = queueRequestService.process(payloads);

        while (!requestQueue.isEmpty()) {
            HttpRequest request = requestQueue.poll();
            HttpResponse<String> response = requestHandler.sendRequest(request);
            String translatedText = responseParser.parseTextTranslation(response);

            result.append(translatedText);
        }

        return result.toString().trim();
    }
}
