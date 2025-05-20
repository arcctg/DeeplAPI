package org.arcctg.service.api;

import org.arcctg.service.impl.TranslationAsyncService;
import org.arcctg.service.impl.TranslationSyncService;

public interface TranslationServiceFactory {

    TranslationSyncService createSync(RequestHandlerService requestHandler);

    TranslationAsyncService createAsync(RequestHandlerService requestHandler);
}
