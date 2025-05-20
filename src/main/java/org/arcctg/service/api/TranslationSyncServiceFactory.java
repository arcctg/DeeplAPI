package org.arcctg.service.api;

import org.arcctg.service.impl.TranslationSyncService;

public interface TranslationSyncServiceFactory {

    TranslationSyncService create(RequestHandlerService requestHandler);
}
