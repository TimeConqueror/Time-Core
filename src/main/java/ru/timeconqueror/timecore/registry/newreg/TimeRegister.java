package ru.timeconqueror.timecore.registry.newreg;

import net.minecraftforge.eventbus.api.IEventBus;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.util.Pair;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class TimeRegister {
    private final String modid;
    @Nullable
    private Class<?> owner;

    public TimeRegister(String modid) {
        this.modid = modid;
    }

    public abstract void regToBus(IEventBus bus);

    public String getModid() {
        return modid;
    }

    public void setOwner(Class<?> owner) {
        this.owner = owner;
    }

    public @Nullable Class<?> getOwner() {
        return owner;
    }

    public void withErrorCatching(String action, Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable e) {
            String culpritInfo = getOwner() != null ? "Currently handling stuff received from class: " + getOwner().getName() : "Unknown owner";
            throw new RuntimeException("Caught exception during " + action + ". " + culpritInfo);
        }
    }

    public void withErrorCatching(String action, Runnable runnable, Supplier<List<Pair<?, ?>>> extraInfo) {
        try {
            runnable.run();
        } catch (Throwable e) {
            String culpritInfo = getOwner() != null ? "Currently handling stuff received from class: " + getOwner().getName() + "." : "Unknown owner.";
            String extra = "Extra Info:\n" + extraInfo.get().stream().map(pair -> pair.getA().toString() + " -> " + pair.getB().toString() + "\n").collect(Collectors.joining());
            throw new RuntimeException("Caught exception during " + action + ". \n" + culpritInfo + "\n" + extra, e);
        }
    }
}
