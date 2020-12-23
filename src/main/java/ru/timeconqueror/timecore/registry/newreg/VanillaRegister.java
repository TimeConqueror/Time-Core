package ru.timeconqueror.timecore.registry.newreg;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import ru.timeconqueror.timecore.registry.InsertablePromised;
import ru.timeconqueror.timecore.registry.Promised;
import ru.timeconqueror.timecore.util.Temporal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * You can {@link SimpleVanillaRegister} it as a wrapper for all vanilla registries, which don't have forge wrapper.
 * All values will be registered on the main thread on {@link FMLCommonSetupEvent}
 */
public abstract class VanillaRegister<T> extends TimeRegister {
    private final Registry<? super T> registry;
    private final Temporal<List<Entry<? extends T>>> entries = Temporal.of(new ArrayList<>());

    public VanillaRegister(String modId, Registry<? super T> registry) {
        super(modId);
        this.registry = registry;
    }

    protected <I extends T> Promised<I> registerEntry(String name, Supplier<I> entrySup) {
        InsertablePromised<I> promised = new InsertablePromised<>(new ResourceLocation(getModId(), name));
        entries.get().add(new Entry<>(promised, entrySup));

        return promised;
    }

    @Override
    public void regToBus(IEventBus bus) {
        super.regToBus(bus);
        bus.addListener(this::onSetup);
    }

    private void onSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            entries.transferAndRemove(entries -> {
                for (Entry<? extends T> entry : entries) {
                    Registry.register(registry, entry.getId(), entry.pull());
                }
            });
        });
    }

    public static class RegisterChain<T> {
        private final Promised<T> promised;

        public RegisterChain(Promised<T> promised) {
            this.promised = promised;
        }

        public Promised<T> asPromise() {
            return promised;
        }

        public ResourceLocation getRegistryName() {
            return promised.getId();
        }
    }

    private static class Entry<T> {
        private final InsertablePromised<T> promised;
        private final Supplier<T> entrySup;

        public Entry(InsertablePromised<T> promised, Supplier<T> entrySup) {
            this.promised = promised;
            this.entrySup = entrySup;
        }

        public ResourceLocation getId() {
            return promised.getId();
        }

        private T pull() {
            T val = entrySup.get();
            promised.insert(val);
            return val;
        }
    }
}
