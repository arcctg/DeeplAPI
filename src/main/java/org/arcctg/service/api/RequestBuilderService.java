package org.arcctg.service.api;

import com.google.inject.ImplementedBy;
import java.net.http.HttpRequest;
import org.arcctg.service.impl.request.DefaultRequestBuilderService;

@ImplementedBy(DefaultRequestBuilderService.class)
public interface RequestBuilderService {
    HttpRequest buildDefault(String payload);
}
