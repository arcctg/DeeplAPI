package org.arcctg.utils;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.Random;
import lombok.SneakyThrows;

public class Utility {

    private static final HttpClient client = HttpClient.newBuilder().build();

    private Utility(){}

    public static IdGenerator getIdGenerator() {
        final int min = 100_000;
        final int max = 100_000_000;

        int randomId = new Random().nextInt(min, max);

        return new IdGenerator(randomId);
    }

    public static long generateTimestamp(List<String> sentences) {
        long now = System.currentTimeMillis();
        int iCount = 1;

        for (String sentence : sentences) {
            iCount += countOccurrences(sentence, "i");
        }

        return calculateValidTimestamp(now, iCount);
    }

    private static int countOccurrences(String sentence, String substring) {
        return sentence.split(substring, -1).length - 1;
    }

    private static long calculateValidTimestamp(long timestamp, int iCount) {
        return iCount != 0 ? timestamp + (iCount - (timestamp % iCount)) : timestamp;
    }

    @SneakyThrows
    public static HttpResponse<String> sendRequest(HttpRequest request) {
        return client.send(request, BodyHandlers.ofString());
    }
}
