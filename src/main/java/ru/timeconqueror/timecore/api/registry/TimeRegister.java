package ru.timeconqueror.timecore.api.registry;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.util.Pair;
import ru.timeconqueror.timecore.storage.Features;
import ru.timeconqueror.timecore.storage.Storage;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

//TODO auto unreg
public abstract class TimeRegister {
    private final String modId;
    protected final Features modFeatures;
    @Nullable
    private Class<?> owner;
    private final AtomicReference<RuntimeException> error = new AtomicReference<>();

    public TimeRegister(String modId) {
        this.modId = modId;
        modFeatures = Storage.getFeatures(modId);
    }

    @OverridingMethodsMustInvokeSuper
    public void regToBus(IEventBus modEventBus) {
        modEventBus.addListener(this::handleLoadException);
    }

    private void handleLoadException(FMLLoadCompleteEvent event) {
        RuntimeException e = error.get();

        if (e != null) {
            throw e;
        }
    }

    public String getModId() {
        return modId;
    }

    public void setOwner(Class<?> owner) {
        this.owner = owner;
    }

    public @Nullable Class<?> getOwner() {
        return owner;
    }

    public void catchErrors(Class<? extends Event> eventClass, Runnable runnable) {
        catchErrors(eventClass.getName(), runnable);
    }

    public void catchErrors(String action, Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable e) {
            String culpritInfo = getOwner() != null ? "Currently handling stuff from class: " + getOwner().getName() : "Unknown owner";
            storeException(new RuntimeException("Caught exception during " + action + ". " + culpritInfo, e));
        }
    }

    public void catchErrors(String action, Runnable runnable, Supplier<List<Pair<?, ?>>> extraInfo) {
        try {
            runnable.run();
        } catch (Throwable e) {
            String culpritInfo = getOwner() != null ? "Currently handling stuff from class: " + getOwner().getName() + "." : "Unknown owner.";
            String extra = "Extra Info:\n" + extraInfo.get().stream().map(pair -> pair.left().toString() + " -> " + pair.right().toString() + "\n").collect(Collectors.joining());
            RuntimeException exception = new RuntimeException("Caught exception during " + action + ". \n" + culpritInfo + "\n" + extra, e);
            storeException(exception);
        }
    }

    protected void storeException(RuntimeException exception) {
        error.compareAndSet(null, exception);
    }
}
