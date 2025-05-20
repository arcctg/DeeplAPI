package org.arcctg.service.api;

import java.net.http.HttpResponse;
import java.util.List;
import org.arcctg.deepl.model.dto.common.Sentence;

public interface ResponseParserService {

    List<Sentence> parseTextSegmentation(HttpResponse<String> httpResponse);

    String parseTextTranslation(HttpResponse<String> httpResponse);
}
