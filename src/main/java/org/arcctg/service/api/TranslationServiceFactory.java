package org.arcctg.service.api;

import org.arcctg.service.impl.translation.TranslationAsyncService;
import org.arcctg.service.impl.translation.TranslationSyncService;

public interface TranslationServiceFactory {

    TranslationSyncService createSync(RequestHandlerService requestHandler);

    TranslationAsyncService createAsync(RequestHandlerService requestHandler);
}
