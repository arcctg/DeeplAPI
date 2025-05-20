package org.arcctg;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.SneakyThrows;
import org.arcctg.config.DeeplModule;
import org.arcctg.deepl.client.DeeplClient;
import org.arcctg.deepl.model.Language;
import org.arcctg.deepl.model.SourceTargetLangs;
import org.arcctg.service.api.RequestHandlerService;
import org.arcctg.service.api.TranslationService;
import org.arcctg.service.api.TranslationSyncServiceFactory;
import org.arcctg.service.impl.DefaultRequestHandler;
import org.arcctg.service.impl.LoggingObserver;
import org.arcctg.service.impl.MetricsObserver;
import org.arcctg.service.impl.RetryRequestHandlerDecorator;
import org.arcctg.service.impl.TranslationCacheService;
import org.arcctg.service.impl.TranslationObservableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    @SneakyThrows
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new DeeplModule());

        RequestHandlerService requestHandler = new RetryRequestHandlerDecorator(
            new DefaultRequestHandler(), 2, 10_000
        );

        TranslationSyncServiceFactory factory = injector.getInstance(
            TranslationSyncServiceFactory.class);
        TranslationService syncService = factory.create(requestHandler);

        TranslationObservableService translationService = new TranslationObservableService(
            new TranslationCacheService(syncService, 100)
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