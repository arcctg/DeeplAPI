package org.arcctg.service.impl.translation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.arcctg.deepl.model.dto.common.Sentence;
import org.arcctg.service.api.PayloadBuilderService;
import org.arcctg.service.api.RequestBuilderService;
import org.arcctg.service.api.RequestHandlerService;
import org.arcctg.service.api.ResponseParserService;
import org.arcctg.service.api.SegmentationService;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class DefaultSegmentationService implements SegmentationService {

    private final RequestHandlerService requestHandler;
    private final PayloadBuilderService payloadBuilderService;
    private final RequestBuilderService requestBuilderService;
    private final ResponseParserService responseParser;

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
