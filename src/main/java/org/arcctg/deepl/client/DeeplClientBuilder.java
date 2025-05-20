package org.arcctg.deepl.client;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.List;
import org.arcctg.config.DeeplModule;
import org.arcctg.service.api.Observer;
import org.arcctg.service.api.RequestHandlerService;
import org.arcctg.service.api.TranslationService;
import org.arcctg.service.api.TranslationServiceFactory;
import org.arcctg.service.impl.DefaultRequestHandler;
import org.arcctg.service.impl.RetryRequestHandlerDecorator;
import org.arcctg.service.impl.TranslationCacheService;
import org.arcctg.service.impl.TranslationObservableService;

public class DeeplClientBuilder {

    private RequestHandlerService requestHandler = new DefaultRequestHandler();
    private List<Observer> observers;
    private boolean enableAsyncRequests;
    private int cacheSize;

    public DeeplClientBuilder withRetryHandler(int maxRetries, int delayMs) {
        this.requestHandler = new RetryRequestHandlerDecorator(
            new DefaultRequestHandler(), maxRetries, delayMs
        );
        return this;
    }

    public DeeplClientBuilder withAsyncRequests(boolean enable) {
        this.enableAsyncRequests = enable;
        return this;
    }

    public DeeplClientBuilder withCacheSize(int size) {
        this.cacheSize = size;
        return this;
    }

    public DeeplClientBuilder withObservers(List<Observer> observers) {
        this.observers = observers;
        return this;
    }

    public DeeplClient build() {
        TranslationServiceFactory factory = createServiceFactory();
        TranslationService translationService = createBaseTranslationService(factory);

        translationService = applyCacheIfEnabled(translationService);
        translationService = applyObserversIfEnabled(translationService);

        return new DeeplClient(translationService);
    }

    private TranslationServiceFactory createServiceFactory() {
        Injector injector = Guice.createInjector(new DeeplModule());

        return injector.getInstance(TranslationServiceFactory.class);
    }

    private TranslationService createBaseTranslationService(TranslationServiceFactory factory) {
        return enableAsyncRequests
            ? factory.createAsync(requestHandler)
            : factory.createSync(requestHandler);
    }

    private TranslationService applyCacheIfEnabled(TranslationService service) {
        if (cacheSize > 0) {
            return new TranslationCacheService(service, cacheSize);
        }

        return service;
    }

    private TranslationService applyObserversIfEnabled(TranslationService service) {
        if (observers != null && !observers.isEmpty()) {
            TranslationObservableService observableService = new TranslationObservableService(
                service);
            observers.forEach(observableService::subscribe);
            return observableService;
        }

        return service;
    }
}
