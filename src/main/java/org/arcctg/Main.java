package org.arcctg;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.arcctg.deepl.client.DeeplClient;
import org.arcctg.deepl.model.Language;
import org.arcctg.deepl.model.SourceTargetLangs;
import org.arcctg.service.impl.AsyncQueueRequestService;
import org.arcctg.service.impl.DefaultPayloadBuilderService;
import org.arcctg.service.impl.DefaultRequestBuilderService;
import org.arcctg.service.impl.DefaultResponseParserService;
import org.arcctg.service.impl.DefaultSegmentationService;
import org.arcctg.service.impl.LoggingObserver;
import org.arcctg.service.impl.MetricsObserver;
import org.arcctg.service.impl.TranslationCacheService;
import org.arcctg.service.impl.TranslationObservableService;
import org.arcctg.service.impl.TranslationSyncService;
import org.arcctg.util.handler.impl.DefaultRequestHandler;
import org.arcctg.util.handler.impl.RetryRequestHandlerDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    @SneakyThrows
    public static void main(String[] args) {
        TranslationObservableService translationService =
            new TranslationObservableService(
                new TranslationCacheService(
                    new TranslationSyncService(
                        new RetryRequestHandlerDecorator(
                            new DefaultRequestHandler(), 2, 10_000
                        ),
                        new DefaultSegmentationService(
                            new DefaultRequestHandler(),
                            new DefaultPayloadBuilderService(),
                            new DefaultRequestBuilderService(),
                            new DefaultResponseParserService(new ObjectMapper())
                        ),
                        new AsyncQueueRequestService(new DefaultRequestBuilderService()),
                        new DefaultPayloadBuilderService(),
                        new DefaultResponseParserService(new ObjectMapper())
                    )
                )
            );

        LoggingObserver loggingObserver = new LoggingObserver("Main");
        MetricsObserver metricsObserver = new MetricsObserver();

        translationService.subscribe(loggingObserver);
        translationService.subscribe(metricsObserver);

        DeeplClient client = new DeeplClient(translationService);
        var langPair = new SourceTargetLangs(Language.ENGLISH, Language.UKRAINIAN);
        String text = "Hello world! How are you?";

        logger.info("{}\n", client.translate(text, langPair));

        translationService.unsubscribe(loggingObserver);

        logger.info(client.translate(text, langPair));
    }
}