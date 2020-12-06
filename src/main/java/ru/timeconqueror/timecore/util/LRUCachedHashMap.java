package ru.timeconqueror.timecore.util;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Map which can delete least recently used values when it reaches the maximum capacity.
 */
public class LRUCachedHashMap<K, V> {
    private final int maxCacheSize;

    private final Map<K, Node<K, V>> cached = new HashMap<>();
    private Node<K, V> head = null;
    private Node<K, V> tail = null;

    public LRUCachedHashMap(int maxCacheSize) {
        Requirements.greaterOrEqualsThan(maxCacheSize, 0);
        this.maxCacheSize = maxCacheSize;
    }

    public int getCurrentCacheSize() {
        return cached.size();
    }

    public boolean isFull() {
        return getCurrentCacheSize() == maxCacheSize;
    }

    @Nullable
    public V getCached(K key) {
        Node<K, V> node = cached.get(key);
        if (node != null) {
            delete(node);
            return insert(node);
        }

        return null;
    }

    public V getOrCache(K key, Function<K, V> compute) {
        V cached = getCached(key);
        if (cached == null) {
            if (isFull()) {
                delete(tail, true);
            }
            return insert(new Node<>(key, compute.apply(key)), true);
        } else {
            return cached;
        }
    }

    private void linkNodes(@Nullable Node<K, V> prev, @Nullable Node<K, V> next) {
        if (prev != null) prev.next = next;
        if (next != null) next.prev = prev;
    }

    private void setHead(@Nullable Node<K, V> node) {
        head = node;
        if (node != null) node.prev = null;
    }

    private void setTail(@Nullable Node<K, V> node) {
        tail = node;
        if (node != null) node.next = null;
    }

    private void delete(Node<K, V> node) {
        delete(node, false);
    }

    private void delete(Node<K, V> node, boolean clearCache) {
        if (node == head) setHead(node.next);
        if (node == tail) setTail(node.prev);

        linkNodes(node.prev, node.next);

        if (clearCache) cached.remove(node.key);
    }

    private V insert(Node<K, V> node) {
        return insert(node, false);
    }

    private V insert(Node<K, V> node, boolean updateCache) {
        linkNodes(node, head);
        setHead(node);
        if (tail == null) setTail(node);
        if (updateCache) cached.put(node.key, node);

        return node.value;
    }

    private static class Node<K, V> {
        private final K key;
        private final V value;
        private Node<K, V> prev = null;
        private Node<K, V> next = null;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}