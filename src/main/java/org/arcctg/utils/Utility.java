package org.arcctg.utils;

import java.util.Random;

public class Utility {

    private Utility(){}

    public static Long generateId() {
        final int min = 100_000;
        final int max = 100_000_000;

        return new Random().nextLong(min, max);
    }
}
