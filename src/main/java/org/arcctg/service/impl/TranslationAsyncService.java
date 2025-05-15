package org.arcctg.service.impl;

import static org.arcctg.deepl.builder.PayloadBuilder.buildForAllSentences;
import static org.arcctg.deepl.builder.RequestBuilder.buildDefault;
import static org.arcctg.deepl.parser.ResponseParser.parseTextTranslation;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.arcctg.deepl.model.SourceTargetLangs;
import org.arcctg.service.api.SegmentationService;
import org.arcctg.service.api.TranslationService;
import org.arcctg.deepl.model.dto.common.Sentence;
import org.arcctg.util.handler.api.RequestHandler;
import org.arcctg.util.handler.impl.DefaultRequestHandler;

public class TranslationAsyncService implements TranslationService {
    private final SegmentationService segmentationService;
    private final RequestHandler requestHandler;

    public TranslationAsyncService() {
        this(new DefaultRequestHandler());
    }

    public TranslationAsyncService(RequestHandler requestHandler) {
        this.segmentationService = new SegmentationServiceImpl();
        this.requestHandler = requestHandler;
    }

    @Override
    public String process(String text, SourceTargetLangs langPair) {
        List<Sentence> allSentences = segmentationService.process(text);

        return translateSentences(allSentences, langPair);
    }

    private String translateSentences(List<Sentence> sentences, SourceTargetLangs langPair) {
        List<String> payloads = buildForAllSentences(sentences, langPair);
        List<Supplier<String>> tasks = buildTranslationTasks(payloads);
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

    private List<Supplier<String>> buildTranslationTasks(List<String> payloads) {
        return payloads.stream()
                .map(payload -> (Supplier<String>) () -> {
                    HttpRequest request = buildDefault(payload);
                    HttpResponse<String> response = requestHandler.sendRequest(request);

                    return parseTextTranslation(response.body());
                }).toList();
    }
}
