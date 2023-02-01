package ru.timeconqueror.timecore.api.util.holder;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public record Pair<L, R>(L left, R right) {
    public static <L, R> Pair<L, R> of(L left, R right) {
        return new Pair<>(left, right);
    }

    @Override
    public String toString() {
        return "{" + left + " -> " + right + "}";
    }

    public <NL> Pair<NL, R> mapKey(Function<L, NL> keyMapper) {
        return map(keyMapper, r -> r);
    }

    public <NR> Pair<L, NR> mapValue(Function<R, NR> valueMapper) {
        return map(l -> l, valueMapper);
    }

    public <NL, NR> Pair<NL, NR> map(Function<L, NL> keyMapper, Function<R, NR> valueMapper) {
        return new Pair<>(keyMapper.apply(left), valueMapper.apply(right));
    }

    public static <A, B> Collector<Pair<A, B>, ?, Map<A, B>> toMap() {
        return Collectors.toMap(Pair::left, Pair::right);
    }

    public static <A, B> Collector<Pair<A, B>, ?, ImmutableMap<A, B>> toImmutableMap() {
        return ImmutableMap.toImmutableMap(Pair::left, Pair::right);
    }
}