package org.arcctg.service.api;

import com.google.inject.ImplementedBy;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.arcctg.service.impl.DefaultRequestHandler;

@ImplementedBy(DefaultRequestHandler.class)
public interface RequestHandlerService {

    HttpResponse<String> sendRequest(HttpRequest request);

}
