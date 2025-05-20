package org.arcctg.service.impl;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.arcctg.deepl.model.SourceTargetLangs;
import org.arcctg.deepl.model.dto.common.Sentence;
import org.arcctg.service.api.PayloadBuilderService;
import org.arcctg.service.api.QueueRequestService;
import org.arcctg.service.api.RequestHandlerService;
import org.arcctg.service.api.ResponseParserService;
import org.arcctg.service.api.SegmentationService;
import org.arcctg.service.api.TranslationService;

public class TranslationAsyncService implements TranslationService {

    private final RequestHandlerService requestHandler;
    private final SegmentationService segmentationService;
    private final QueueRequestService queueRequestService;
    private final PayloadBuilderService payloadBuilderService;
    private final ResponseParserService responseParser;

    @Inject
    public TranslationAsyncService(
        @Assisted RequestHandlerService requestHandler,
        SegmentationService segmentationService,
        QueueRequestService queueRequestService,
        PayloadBuilderService payloadBuilderService,
        ResponseParserService responseParser) {
        this.requestHandler = requestHandler;
        this.segmentationService = segmentationService;
        this.queueRequestService = queueRequestService;
        this.payloadBuilderService = payloadBuilderService;
        this.responseParser = responseParser;
    }

    @Override
    public String process(String text, SourceTargetLangs langPair) {
        List<Sentence> allSentences = segmentationService.process(text);

        return translateSentences(allSentences, langPair);
    }

    private String translateSentences(List<Sentence> sentences, SourceTargetLangs langPair) {
        List<String> payloads = payloadBuilderService.buildForAllSentences(sentences, langPair);
        Queue<HttpRequest> requestQueue = queueRequestService.process(payloads);
        List<Supplier<String>> tasks = buildTranslationTasks(requestQueue);
        CompletableFuture<String>[] futures = createFutures(tasks);

        return collectTranslations(futures);
    }

    @SuppressWarnings("unchecked")
    private CompletableFuture<String>[] createFutures(List<Supplier<String>> tasks) {
        return tasks.stream()
            .map(CompletableFuture::supplyAsync)
            .toArray(CompletableFuture[]::new);
    }

    private String collectTranslations(CompletableFuture<String>[] futures) {
        return CompletableFuture.allOf(futures)
            .thenApply(v -> Stream.of(futures)
                .map(CompletableFuture::join)
                .collect(Collectors.joining("")))
            .join();
    }

    private List<Supplier<String>> buildTranslationTasks(Queue<HttpRequest> requestQueue) {
        return requestQueue.stream()
            .map(request -> (Supplier<String>) () ->
                responseParser.parseTextTranslation(requestHandler.sendRequest(request))
            ).toList();
    }
}
