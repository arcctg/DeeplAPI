package org.arcctg.service.impl;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.arcctg.deepl.model.dto.common.Sentence;
import org.arcctg.service.api.PayloadBuilderService;
import org.arcctg.service.api.RequestBuilderService;
import org.arcctg.service.api.ResponseParserService;
import org.arcctg.service.api.SegmentationService;
import org.arcctg.util.handler.api.RequestHandler;

public class DefaultSegmentationService implements SegmentationService {

    private final RequestHandler requestHandler;
    private final PayloadBuilderService payloadBuilderService;
    private final RequestBuilderService requestBuilderService;
    private final ResponseParserService responseParser;

    public DefaultSegmentationService(
        RequestHandler requestHandler,
        PayloadBuilderService payloadBuilderService,
        RequestBuilderService requestBuilderService,
        ResponseParserService responseParser) {
        this.requestHandler = requestHandler;
        this.payloadBuilderService = payloadBuilderService;
        this.requestBuilderService = requestBuilderService;
        this.responseParser = responseParser;
    }

    @Override
    public List<Sentence> process(String text) {
        HttpResponse<String> response = requestTextSegmentation(text);

        return responseParser.parseTextSegmentation(response);
    }

    private HttpResponse<String> requestTextSegmentation(String text) {
        String payload = payloadBuilderService.buildForTextSegmentation(text);
        HttpRequest request = requestBuilderService.buildDefault(payload);

        return requestHandler.sendRequest(request);
    }
}
