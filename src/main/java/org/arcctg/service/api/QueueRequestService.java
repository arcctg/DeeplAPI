package org.arcctg.service.api;

import java.net.http.HttpRequest;
import java.util.List;
import java.util.Queue;

public interface QueueRequestService {
    Queue<HttpRequest> process(List<String> payloads);
}
