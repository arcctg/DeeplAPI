package org.arcctg.service.impl;

import static org.arcctg.deepl.builder.PayloadBuilder.buildForTextSegmentation;
import static org.arcctg.deepl.builder.RequestBuilder.buildDefault;
import static org.arcctg.deepl.parser.ResponseParser.parseTextSegmentation;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.arcctg.service.api.SegmentationService;
import org.arcctg.deepl.model.dto.common.Sentence;
import org.arcctg.util.handler.api.RequestHandler;
import org.arcctg.util.handler.impl.DefaultRequestHandler;

public class SegmentationServiceImpl implements SegmentationService {

    private final RequestHandler requestHandler = new DefaultRequestHandler();

    @Override
    public List<Sentence> process(String text) {
        String jsonResponse = requestTextSegmentation(text);

        return parseTextSegmentation(jsonResponse);
    }

    private String requestTextSegmentation(String text) {
        String payload = buildForTextSegmentation(text);
        HttpRequest request = buildDefault(payload);
        HttpResponse<String> response = requestHandler.sendRequest(request);

        return response.body();
    }
}
