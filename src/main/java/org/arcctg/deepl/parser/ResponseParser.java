package org.arcctg.deepl.parser;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import org.arcctg.deepl.model.dto.response.Beam;
import org.arcctg.deepl.model.dto.response.Chunk;
import org.arcctg.deepl.model.dto.response.ResponseTemplate;
import org.arcctg.deepl.model.dto.response.Text;
import org.arcctg.deepl.model.dto.response.Translation;
import org.arcctg.deepl.model.dto.common.Sentence;

public class ResponseParser {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private ResponseParser() {}

    @SneakyThrows
    public static List<Sentence> parseTextSegmentation(HttpResponse<String> httpResponse) {
        checkForException(httpResponse);

        ResponseTemplate response = objectMapper.readValue(httpResponse.body(), ResponseTemplate.class);

        List<Sentence> sentences = new ArrayList<>();
        long idCounter = 1;

        for (Text textBlock : response.getResult().getTexts()) {
            for (Chunk chunk : textBlock.getChunks()) {
                for (Sentence sentence : chunk.getSentences()) {
                    sentence.setId(idCounter++);
                    sentences.add(sentence);

                    idCounter = idCounter >= 99 ? 1 : idCounter;
                }
            }
        }

        return sentences;
    }

    @SneakyThrows
    public static String parseTextTranslation(HttpResponse<String> httpResponse) {
        checkForException(httpResponse);

        ResponseTemplate response = objectMapper.readValue(httpResponse.body(), ResponseTemplate.class);

        StringBuilder stringBuilder = new StringBuilder();

        for (Translation translation : response.getResult().getTranslations()) {
            for (Beam beam : translation.getBeams()) {
                for (Sentence sentence : beam.getSentences()) {
                    stringBuilder.append(" ").append(new String(sentence.getText().getBytes(),
                            StandardCharsets.UTF_8));
                }
            }
        }

        return stringBuilder.toString();
    }

    private static void checkForException(HttpResponse<String> httpResponse) {
        if (httpResponse.statusCode() != 200) {
            throw new RuntimeException("Error: %d - %s".formatted(httpResponse.statusCode(),
                    httpResponse.body()));
        }
    }
}
