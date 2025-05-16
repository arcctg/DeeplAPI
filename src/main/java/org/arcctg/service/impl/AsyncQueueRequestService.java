package org.arcctg.service.impl;

import org.arcctg.deepl.builder.RequestBuilder;
import org.arcctg.service.api.QueueRequestService;

import java.net.http.HttpRequest;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class AsyncQueueRequestService implements QueueRequestService {

    @Override
    public Queue<HttpRequest> process(List<String> payloads) {
        return payloads.parallelStream()
                .map(RequestBuilder::buildDefault)
                .collect(Collectors.toCollection(ConcurrentLinkedQueue::new));
    }
}
