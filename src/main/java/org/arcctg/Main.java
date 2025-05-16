package org.arcctg;

import lombok.SneakyThrows;
import org.arcctg.deepl.client.DeeplClient;
import org.arcctg.deepl.model.Language;
import org.arcctg.deepl.model.SourceTargetLangs;
import org.arcctg.service.impl.*;
import org.arcctg.util.handler.impl.DefaultRequestHandler;
import org.arcctg.util.handler.impl.RetryRequestHandlerDecorator;

public class Main {

    @SneakyThrows
    public static void main(String[] args) {
        TranslationObservableServiceDecorator translationService =
                new TranslationObservableServiceDecorator(
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

        System.out.println(client.translate(text, langPair) + "\n");

        translationService.unsubscribe(loggingObserver);

        System.out.println(client.translate(text, langPair));
    }
}