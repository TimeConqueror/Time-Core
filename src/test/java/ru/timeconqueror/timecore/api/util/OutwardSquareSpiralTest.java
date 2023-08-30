package ru.timeconqueror.timecore.api.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Slf4j
public class OutwardSquareSpiralTest {
    @Test
    public void testIndexToPosMappings() {
        AtomicInteger ref = new AtomicInteger();

        Thread thread = new Thread(() -> {
            while (true) {
                log.info("OutwardSquareSpiralTest.run: processed: {}/{}", ref.get(), Integer.MAX_VALUE);
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.setDaemon(true);
        thread.start();

        long misMappings = IntStream.rangeClosed(0, Integer.MAX_VALUE)
                .parallel()
                .mapToObj(value -> {
                    ref.getAndIncrement();
                    Vec2i offset = MathUtils.OutwardSquareSpiral.offsetByIndex(value);
                    return new Data(value, offset, MathUtils.OutwardSquareSpiral.softIndexByOffset(offset));
                })
                .filter(data -> data.providedIndex != data.gotIndex)
                .count();

        Assertions.assertEquals(0, misMappings);
    }

    public record Data(int providedIndex, Vec2i offset, int gotIndex) {

    }
}
