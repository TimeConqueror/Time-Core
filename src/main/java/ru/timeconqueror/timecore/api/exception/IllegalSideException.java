package ru.timeconqueror.timecore.api.exception;

import net.minecraftforge.fml.LogicalSide;

public class IllegalSideException extends RuntimeException {
    private final LogicalSide side;

    public static void notOnServer() {
        throw new IllegalSideException(LogicalSide.CLIENT);
    }

    public static void notOnClient() {
        throw new IllegalSideException(LogicalSide.SERVER);
    }

    private IllegalSideException(LogicalSide illegalSide) {
        super("Called from illegal side: " + illegalSide);
        this.side = illegalSide;
    }

    public LogicalSide getSide() {
        return side;
    }
}
