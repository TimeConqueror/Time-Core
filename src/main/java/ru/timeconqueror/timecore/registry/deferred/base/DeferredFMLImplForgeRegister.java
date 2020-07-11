package ru.timeconqueror.timecore.registry.deferred.base;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import ru.timeconqueror.timecore.registry.TimeAutoRegistrable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Deferred Forge Register, which works via FML's {@link DeferredRegister}
 * <p>
 * To work inheritor needs to be in a static field in registry class and be annotated with {@link TimeAutoRegistrable}.
 * Extra params in this annotation are ignored.
 */
public abstract class DeferredFMLImplForgeRegister<T extends IForgeRegistryEntry<T>> extends DeferredForgeRegister<T> {
    protected final DeferredRegister<T> deferredRegister;

    protected List<Runnable> clientOnlyRunnables = new ArrayList<>();

    public DeferredFMLImplForgeRegister(IForgeRegistry<T> reg, String modid) {
        super(reg, modid);
        this.deferredRegister = new DeferredRegister<>(reg, modid);
    }

    public void regToBus(IEventBus bus) {
        super.regToBus(bus);
        deferredRegister.register(bus);
    }

    @Override
    protected EventPriority getRegPriority() {
        return EventPriority.LOWEST;
    }

    protected void onRegEvent(RegistryEvent.Register<T> event) {
        if (isOnClient()) {
            clientOnlyRunnables.forEach(Runnable::run);
        }

        clientOnlyRunnables = null;
    }

    public class Registrator {
        protected final RegistryObject<T> registryObject;

        protected Registrator(String name, Supplier<? extends T> sup) {
            this.registryObject = deferredRegister.register(name, sup);
        }

        /**
         * End method which returns the final object, from which you can retrieve your registry entry.
         */
        public RegistryObject<T> end() {
            return getRegistryObject();
        }

        public RegistryObject<T> getRegistryObject() {
            return registryObject;
        }

        public ResourceLocation getRegistryKey() {
            return getRegistryObject().getId();
        }

        public String getName() {
            return getRegistryKey().getPath();
        }

        protected Registrator runOnlyForClient(Runnable runnable) {
            if (isOnClient()) {
                clientOnlyRunnables.add(runnable);
            }

            return this;
        }
    }

    protected boolean isOnClient() {
        return FMLEnvironment.dist == Dist.CLIENT;
    }
}
