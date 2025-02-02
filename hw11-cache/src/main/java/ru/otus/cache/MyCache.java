package ru.otus.cache;

import ru.otus.cache.listener.HwListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class MyCache<K, V> implements HwCache<K, V> {

    Map<K, V> cache = new WeakHashMap<>();

    List<HwListener<K, V>> listeners = new ArrayList<>();


    @Override
    public void put(K key, V value) {
        cache.put(key, value);
        runListeners(key, value, "added");
    }

    @Override
    public void remove(K key) {
        var value = cache.remove(key);
        runListeners(key, value, "removed");
    }

    @Override
    public V get(K key) {
        var value = cache.get(key);
        runListeners(key, value, "got");
        return value;
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        listeners.remove(listener);
    }


    private void runListeners(K key, V value, String action) {
        listeners.forEach(listener -> listener.notify(key, value, action));
    }

}
