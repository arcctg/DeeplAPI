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

    private List<Observer> observers;
    private Injector injector;

    private boolean enableRetryHandler;
    private boolean enableAsyncRequests;

    private int maxRetries;
    private int delayMs;
    private int cacheSize;

    public DeeplClientBuilder withRetryHandler(int maxRetries, int delayMs) {
        this.enableRetryHandler = true;
        this.maxRetries = maxRetries;
        this.delayMs = delayMs;

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
        injector = Guice.createInjector(new DeeplModule());

        TranslationServiceFactory factory = createServiceFactory();
        RequestHandlerService requestHandler = createRequestHandler();
        TranslationService translationService = createBaseTranslationService(factory,
            requestHandler);

        translationService = applyCacheIfEnabled(translationService);
        translationService = applyObserversIfEnabled(translationService);

        return new DeeplClient(translationService);
    }

    private RequestHandlerService createRequestHandler() {
        RequestHandlerService handlerService = injector.getInstance(DefaultRequestHandler.class);

        return enableRetryHandler
            ? new RetryRequestHandlerDecorator(handlerService, maxRetries, delayMs)
            : handlerService;
    }

    private TranslationServiceFactory createServiceFactory() {
        return injector.getInstance(TranslationServiceFactory.class);
    }

    private TranslationService createBaseTranslationService(TranslationServiceFactory factory,
        RequestHandlerService requestHandler) {
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
