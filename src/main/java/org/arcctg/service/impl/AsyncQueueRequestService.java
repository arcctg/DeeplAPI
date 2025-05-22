package org.arcctg.service.impl;

import com.google.inject.Inject;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.arcctg.service.api.QueueRequestService;
import org.arcctg.service.api.RequestBuilderService;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class AsyncQueueRequestService implements QueueRequestService {

    private final RequestBuilderService requestBuilderService;

    @Override
    public Queue<HttpRequest> process(List<String> payloads) {
        return payloads.parallelStream()
            .map(requestBuilderService::buildDefault)
            .collect(Collectors.toCollection(ConcurrentLinkedQueue::new));
    }
}
