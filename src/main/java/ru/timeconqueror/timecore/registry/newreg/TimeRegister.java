package ru.timeconqueror.timecore.registry.newreg;

import net.minecraftforge.eventbus.api.IEventBus;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.util.Pair;
import ru.timeconqueror.timecore.devtools.gen.lang.LangGeneratorFacade;
import ru.timeconqueror.timecore.storage.Features;
import ru.timeconqueror.timecore.storage.Storage;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class TimeRegister {
    private final String modId;
    protected final Features modFeatures;
    @Nullable
    private Class<?> owner;

    public TimeRegister(String modId) {
        this.modId = modId;
        modFeatures = Storage.getFeatures(modId);
    }

    public abstract void regToBus(IEventBus bus);

    public String getModId() {
        return modId;
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

    protected LangGeneratorFacade getLangGeneratorFacade() {
        return modFeatures.getLangGeneratorFacade();
    }
}
