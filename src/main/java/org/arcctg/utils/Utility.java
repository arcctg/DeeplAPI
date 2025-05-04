package org.arcctg.utils;

import java.util.List;
import java.util.Random;

public class Utility {

    private Utility(){}

    public static Long generateId() {
        final int min = 100_000;
        final int max = 100_000_000;

        return new Random().nextLong(min, max);
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
}
