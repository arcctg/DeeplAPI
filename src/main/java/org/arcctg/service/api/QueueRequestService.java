package org.arcctg.service.api;

import com.google.inject.ImplementedBy;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Queue;
import org.arcctg.service.impl.AsyncQueueRequestService;

@ImplementedBy(AsyncQueueRequestService.class)
public interface QueueRequestService {
    Queue<HttpRequest> process(List<String> payloads);
}
