package org.arcctg.deepl.service;

import static org.arcctg.deepl.request.PayloadBuilder.buildForTextSegmentation;
import static org.arcctg.deepl.request.RequestBuilder.buildDefault;
import static org.arcctg.deepl.response.ResponseParser.parseTextSegmentation;
import static org.arcctg.utils.Utility.sendRequest;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.arcctg.deepl.service.interfaces.SegmentationService;
import org.arcctg.model.common.Sentence;

public class SegmentationServiceClass implements SegmentationService {

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
