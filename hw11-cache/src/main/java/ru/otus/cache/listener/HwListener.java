package ru.otus.cache.listener;

public interface HwListener<K, V> {
    void notify(K key, V value, String action);
}
