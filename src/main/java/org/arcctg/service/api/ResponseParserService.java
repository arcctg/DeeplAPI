package org.arcctg.service.api;

import com.google.inject.ImplementedBy;
import java.net.http.HttpResponse;
import java.util.List;
import org.arcctg.deepl.model.dto.common.Sentence;
import org.arcctg.service.impl.request.DefaultResponseParserService;

@ImplementedBy(DefaultResponseParserService.class)
public interface ResponseParserService {

    List<Sentence> parseTextSegmentation(HttpResponse<String> httpResponse);

    String parseTextTranslation(HttpResponse<String> httpResponse);
}
