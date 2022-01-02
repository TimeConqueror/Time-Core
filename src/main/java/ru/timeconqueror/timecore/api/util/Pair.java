package ru.timeconqueror.timecore.api.util;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Pair<L, R> {
    private final L left;
    private final R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public static <L, R> Pair<L, R> of(L left, R right) {
        return new Pair<>(left, right);
    }

    //use #left() instead
    @Deprecated //TODO remove in 1.17
    public L getA() {
        return this.left;
    }

    //use #right() instead
    @Deprecated //TODO remove in 1.17
    public R getB() {
        return this.right;
    }

    public L left() {
        return left;
    }

    public R right() {
        return right;
    }

    @Override
    public String toString() {
        return "{" + left + " -> " + right + "}";
    }

    public static <A, B> Collector<Pair<A, B>, ?, Map<A, B>> toMap() {
        return Collectors.toMap(Pair::left, Pair::right);
    }

    public static <A, B> Collector<Pair<A, B>, ?, ImmutableMap<A, B>> toImmutableMap() {
        return ImmutableMap.toImmutableMap(Pair::left, Pair::right);
    }
}