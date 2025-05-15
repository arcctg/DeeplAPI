package org.arcctg;

import lombok.SneakyThrows;
import org.arcctg.deepl.client.DeeplClient;
import org.arcctg.deepl.model.Language;
import org.arcctg.deepl.model.SourceTargetLangs;
import org.arcctg.service.impl.TranslationAsyncService;
import org.arcctg.service.impl.TranslationCacheService;
import org.arcctg.util.handler.impl.RetryRequestHandlerDecorator;

public class Main {

    @SneakyThrows
    public static void main(String[] args) {
        DeeplClient client = new DeeplClient(
                new TranslationCacheService(
                        new TranslationAsyncService(
                                new RetryRequestHandlerDecorator(2, 10_000)
                        )
                )
        );
        var langPair = new SourceTargetLangs(Language.ENGLISH, Language.UKRAINIAN);
        String text = "Hello world! How are you?";

        String translation = client.translate(text, langPair);

        System.out.println(translation);
    }
}