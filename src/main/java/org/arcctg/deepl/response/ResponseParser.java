package org.arcctg.deepl.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import org.arcctg.model.response.*;
import org.arcctg.model.common.Sentence;

public class ResponseParser {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private ResponseParser() {}

    @SneakyThrows
    public static List<Sentence> parseTextSegmentation(String jsonResponse) {
        checkForException(jsonResponse);

        ResponseTemplate response = objectMapper.readValue(jsonResponse, ResponseTemplate.class);

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
    public static String parseTextTranslation(String jsonResponse) {
        checkForException(jsonResponse);

        ResponseTemplate response = objectMapper.readValue(jsonResponse, ResponseTemplate.class);

        StringBuilder stringBuilder = new StringBuilder();

        for (Translation translation : response.getResult().getTranslations()) {
            for (Beam beam : translation.getBeams()) {
                for (Sentence sentence : beam.getSentences()) {
                    stringBuilder.append(" ").append(new String(sentence.getText().getBytes(), StandardCharsets.UTF_8));
                }
            }
        }

        return stringBuilder.toString();
    }

    private static void checkForException(String jsonResponse) {
        if (jsonResponse.contains("Too many requests")) {
            throw new RuntimeException("Too many requests");
        }
    }
}
