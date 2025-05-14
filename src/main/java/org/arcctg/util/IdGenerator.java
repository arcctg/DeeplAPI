package org.arcctg.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class IdGenerator implements Iterator<Integer> {
    private int currentValue;

    public IdGenerator(int startValue) {
        this.currentValue = startValue;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Integer next() {
        if (hasNext()) {
            return ++currentValue;
        } else {
            throw new NoSuchElementException();
        }
    }

    public Integer get() {
        return currentValue;
    }
}
