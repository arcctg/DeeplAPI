package org.arcctg.service.api;

import org.arcctg.deepl.model.dto.common.Sentence;

import java.net.http.HttpRequest;
import java.util.List;
import java.util.Queue;

public interface QueueRequestService {
    Queue<HttpRequest> process(List<String> payloads);
}
