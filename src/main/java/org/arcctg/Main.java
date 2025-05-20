package org.arcctg;

import java.util.List;
import lombok.SneakyThrows;
import org.arcctg.deepl.client.DeeplClient;
import org.arcctg.deepl.model.Language;
import org.arcctg.deepl.model.SourceTargetLangs;
import org.arcctg.service.impl.LoggingObserver;
import org.arcctg.service.impl.MetricsObserver;
import org.arcctg.service.impl.TranslationObservableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    @SneakyThrows
    public static void main(String[] args) {
        TranslationObservableService observableService;
        LoggingObserver loggingObserver = new LoggingObserver("Main");
        MetricsObserver metricsObserver = new MetricsObserver();

        DeeplClient client = DeeplClient.builder()
            .withAsyncRequests(true)
            .withRetryHandler(3, 1000)
            .withCacheSize(100)
            .withObservers(List.of(loggingObserver, metricsObserver))
            .build();

        observableService = (TranslationObservableService) client.getTranslationService();

        var langPair = new SourceTargetLangs(Language.ENGLISH, Language.UKRAINIAN);
        String text = "Hello world! How are you?";

        logger.info("{}\n", client.translate(text, langPair));

        observableService.unsubscribe(loggingObserver);

        logger.info(client.translate(text, langPair));
    }
}