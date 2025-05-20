package org.arcctg.service.api;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public interface RequestHandler {

    HttpResponse<String> sendRequest(HttpRequest request);

}
