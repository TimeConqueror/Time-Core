package ru.timeconqueror.timecore.tests;

import ru.timeconqueror.timecore.api.util.MathUtils;
import ru.timeconqueror.timecore.api.util.Vec2i;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class OutwardSquareSpiralTest {
    public static void run() {
        AtomicInteger ref = new AtomicInteger();

        Thread thread = new Thread(() -> {
            long lastMillis = System.currentTimeMillis();
            while (true) {
                if (System.currentTimeMillis() - 1000 > lastMillis) {
                    System.out.println("OutwardSquareSpiralTest.run: processed: " + ref.get() + "/" + Integer.MAX_VALUE);
                    lastMillis = System.currentTimeMillis();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();

        long count = IntStream.rangeClosed(0, Integer.MAX_VALUE)
                .parallel()
                .mapToObj(value -> {
                    ref.getAndIncrement();
                    Vec2i offset = MathUtils.OutwardSquareSpiral.offsetByIndex(value);
                    return new Data(value, offset, MathUtils.OutwardSquareSpiral.softIndexByOffset(offset));
                })
                .filter(data -> data.providedIndex != data.gotIndex)
                .count();

        if (count > 0) {
            throw new AssertionError(count + " cases failed");
        } else {
            System.out.println("OutwardSquareSpiralTest.run successfully passed.");
        }
    }

    public record Data(int providedIndex, Vec2i offset, int gotIndex) {

    }
}
