package org.arcctg.config;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import org.arcctg.service.api.TranslationSyncServiceFactory;

public class DeeplModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder().build(TranslationSyncServiceFactory.class));
    }
}
