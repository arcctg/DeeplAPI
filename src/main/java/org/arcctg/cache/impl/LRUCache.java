package org.arcctg.cache.impl;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import org.arcctg.cache.spi.Cache;

public class LRUCache<K, V> implements Cache<K, V> {
    private final LinkedHashMap<K, V> cache;
    @Getter
    private final int capacity;

    public LRUCache(int capacity) {
        this.capacity = capacity;

        this.cache = new LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > capacity;
            }
        };
    }

    @Override
    public synchronized V get(K key) {
        return cache.get(key);
    }

    @Override
    public synchronized void put(K key, V value) {
        cache.put(key, value);
    }

    @Override
    public synchronized void clear() {
        cache.clear();
    }

    @Override
    public synchronized int size() {
        return cache.size();
    }

}