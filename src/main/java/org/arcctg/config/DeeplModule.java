package org.arcctg.config;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import java.net.http.HttpClient;
import java.util.concurrent.atomic.AtomicInteger;
import org.arcctg.service.api.TranslationServiceFactory;
import org.arcctg.util.Utility;

public class DeeplModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder().build(TranslationServiceFactory.class));

        bind(AtomicInteger.class)
            .annotatedWith(Names.named("Payload id"))
            .toInstance(new AtomicInteger(Utility.getRandomIdInSetRange()));

        bind(HttpClient.class).toInstance(HttpClient.newHttpClient());
    }
}
