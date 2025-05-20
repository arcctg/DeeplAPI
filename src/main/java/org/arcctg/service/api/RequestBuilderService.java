package org.arcctg.service.api;

import java.net.http.HttpRequest;

public interface RequestBuilderService {
    HttpRequest buildDefault(String payload);
}
