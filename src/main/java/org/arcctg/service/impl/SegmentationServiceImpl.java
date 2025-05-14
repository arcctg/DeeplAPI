package org.arcctg.service.impl;

import static org.arcctg.deepl.builder.PayloadBuilder.buildForTextSegmentation;
import static org.arcctg.deepl.builder.RequestBuilder.buildDefault;
import static org.arcctg.deepl.parser.ResponseParser.parseTextSegmentation;
import static org.arcctg.util.Utility.sendRequest;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.arcctg.service.api.SegmentationService;
import org.arcctg.deepl.model.dto.common.Sentence;

public class SegmentationServiceImpl implements SegmentationService {

    @Override
    public List<Sentence> process(String text) {
        String jsonResponse = requestTextSegmentation(text);

        return parseTextSegmentation(jsonResponse);
    }

    private String requestTextSegmentation(String text) {
        String payload = buildForTextSegmentation(text);
        HttpRequest request = buildDefault(payload);
        HttpResponse<String> response = sendRequest(request);

        return response.body();
    }
}
