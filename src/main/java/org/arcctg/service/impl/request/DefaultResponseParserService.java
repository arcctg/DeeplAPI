package org.arcctg.service.impl.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.arcctg.deepl.model.dto.common.Sentence;
import org.arcctg.deepl.model.dto.response.Beam;
import org.arcctg.deepl.model.dto.response.Chunk;
import org.arcctg.deepl.model.dto.response.ResponseTemplate;
import org.arcctg.deepl.model.dto.response.Text;
import org.arcctg.deepl.model.dto.response.Translation;
import org.arcctg.service.api.ResponseParserService;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class DefaultResponseParserService implements ResponseParserService {

    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public List<Sentence> parseTextSegmentation(HttpResponse<String> httpResponse) {
        checkForException(httpResponse);

        ResponseTemplate response = objectMapper.readValue(httpResponse.body(),
            ResponseTemplate.class);

        return getSentencesFromResponse(response);
    }

    @Override
    @SneakyThrows
    public String parseTextTranslation(HttpResponse<String> httpResponse) {
        checkForException(httpResponse);

        ResponseTemplate response = objectMapper.readValue(httpResponse.body(),
            ResponseTemplate.class);

        return getTranslatedString(response);
    }

    private static List<Sentence> getSentencesFromResponse(ResponseTemplate response) {
        List<Sentence> sentences = new ArrayList<>();
        long idCounter = 1;

        for (Text textBlock : response.getResult().getTexts()) {
            for (Chunk chunk : textBlock.getChunks()) {
                for (Sentence sentence : chunk.getSentences()) {
                    sentence.setId(idCounter++);
                    sentences.add(sentence);
                }
            }
        }

        return sentences;
    }

    private static String getTranslatedString(ResponseTemplate response) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Translation translation : response.getResult().getTranslations()) {
            for (Beam beam : translation.getBeams()) {
                for (Sentence sentence : beam.getSentences()) {
                    stringBuilder.append(" ").append(new String(sentence.getText().getBytes(),
                        StandardCharsets.UTF_8));
                }
            }
        }

        return stringBuilder.toString().trim();
    }

    private static void checkForException(HttpResponse<String> httpResponse) {
        if (httpResponse.statusCode() < 200 || httpResponse.statusCode() >= 300) {
            throw new RuntimeException("%d - %s".formatted(httpResponse.statusCode(),
                httpResponse.body()));
        }
    }
}
