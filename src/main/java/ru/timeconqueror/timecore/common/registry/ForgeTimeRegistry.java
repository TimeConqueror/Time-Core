package ru.timeconqueror.timecore.common.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.Contract;
import ru.timeconqueror.timecore.api.ITimeMod;

import java.util.ArrayList;

/**
 * Registry that should be extended and annotated with {@link ru.timeconqueror.timecore.common.registry.TimeAutoRegistry},
 * if you want to register any object that extends {@link IForgeRegistryEntry}.
 * <p>
 * Examples can be seen here: {@link ru.timeconqueror.timecore.test.registry.TItems}
 */
public abstract class ForgeTimeRegistry<T extends IForgeRegistryEntry<T>> {
    protected ArrayList<EntryWrapper> regList = new ArrayList<>();
    private ITimeMod mod;

    public ForgeTimeRegistry(ITimeMod mod) {
        this.mod = mod;
    }

    public void onFireRegistryEvent(RegistryEvent.Register<T> event) {
        register();

        forceBoundModLoading(() -> {
            for (EntryWrapper t : regList) {
                event.getRegistry().register(t.entry);
            }
        });
    }

    /**
     * Method, where you should do all register stuff.
     */
    public abstract void register();

    public ITimeMod getMod() {
        return mod;
    }

    public String getModID() {
        return getMod().getModID();
    }

    /**
     * Forces game to set bound {@link #mod} as active Mod Container, while {@code runnable} will be called.
     *
     * @param runnable is called after mod is forced to load. Can contain, for example, register functions,
     *                 because Forge starts to warn you, when you try to register SomeMod things while TimeCore is loading.
     */
    protected void forceBoundModLoading(Runnable runnable) {
        ModLoadingContext context = ModLoadingContext.get();

        ModList.get().getModContainerById(getModID()).ifPresent((modContainer) -> {
            ModContainer oldModContainer = context.getActiveContainer();
            context.setActiveContainer(modContainer, context.extension());

            runnable.run();

            context.setActiveContainer(oldModContainer, context.extension());
        });
    }

    public class EntryWrapper {
        private T entry;

        @Contract()
        public EntryWrapper(T entry, String name) {
            name = name.toLowerCase();

            this.entry = entry;
            entry.setRegistryName(new ResourceLocation(getMod().getModID(), name));

            regList.add(this);
        }

        public T getEntry() {
            return entry;
        }
    }
}
