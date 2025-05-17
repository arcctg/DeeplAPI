package org.arcctg;

import lombok.SneakyThrows;
import org.arcctg.deepl.client.DeeplClient;
import org.arcctg.deepl.model.Language;
import org.arcctg.deepl.model.SourceTargetLangs;
import org.arcctg.service.impl.*;
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
                                        )
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