package ru.timeconqueror.timecore.registry.deferred.base;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import ru.timeconqueror.timecore.registry.TimeAutoRegistrable;

import java.util.function.Supplier;

/**
 * Deferred Forge Register, which works via FML's {@link DeferredRegister}
 * <p>
 * To work inheritor needs to be in a static field in registry class and be annotated with {@link TimeAutoRegistrable}.
 * Extra params in this annotation are ignored.
 */
public abstract class DeferredFMLImplForgeRegister<T extends IForgeRegistryEntry<T>> extends DeferredForgeRegister<T> {
    protected final DeferredRegister<T> deferredRegister;

    public DeferredFMLImplForgeRegister(IForgeRegistry<T> reg, String modid) {
        super(reg, modid);
        this.deferredRegister = DeferredRegister.create(reg, modid);
    }

    public void regToBus(IEventBus bus) {
        super.regToBus(bus);
        deferredRegister.register(bus);
    }

    public class Registrator {
        protected final RegistryObject<T> registryObject;

        protected Registrator(String name, Supplier<? extends T> sup) {
            this.registryObject = deferredRegister.register(name, sup);
        }

        /**
         * End method which returns the final object, so here you can retrieve your registry entry.
         */
        public RegistryObject<T> end() {
            return asRegistryObject();
        }

        public RegistryObject<T> asRegistryObject() {
            return registryObject;
        }

        public ResourceLocation getRegistryKey() {
            return asRegistryObject().getId();
        }

        public String getName() {
            return getRegistryKey().getPath();
        }

        protected Registrator runOnlyForClient(Runnable runnable) {
            runTaskOnClientSetup(runnable);
            return this;
        }
    }
}
