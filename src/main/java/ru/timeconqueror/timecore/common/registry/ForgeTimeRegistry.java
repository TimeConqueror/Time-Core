package ru.timeconqueror.timecore.common.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.registries.IForgeRegistryEntry;
import ru.timeconqueror.timecore.api.ITimeMod;

import java.util.ArrayList;

public abstract class ForgeTimeRegistry<T extends IForgeRegistryEntry<T>> {
    protected ArrayList<EntryWrapper> regList = new ArrayList<>();
    private ITimeMod mod;

    public ForgeTimeRegistry(ITimeMod mod) {
        this.mod = mod;
    }

    public void onFireRegistryEvent(RegistryEvent.Register<T> event) {
        register();

        ModLoadingContext context = ModLoadingContext.get();

        ModList.get().getModContainerById(mod.getModID()).ifPresent((modContainer) -> {
            ModContainer oldModContainer = context.getActiveContainer();
            context.setActiveContainer(modContainer, context.extension());

            for (EntryWrapper t : regList) {
                event.getRegistry().register(t.entry);
            }

            context.setActiveContainer(oldModContainer, context.extension());
        });
    }

    /**
     * Method, where you should do all register stuff.
     */
    public abstract void register();

    public ITimeMod getMod() {
        return mod;
    }

    public class EntryWrapper {
        private T entry;

        public EntryWrapper(T entry, String name) {
            this.entry = entry;
            entry.setRegistryName(new ResourceLocation(getMod().getModID(), name));

            regList.add(this);
        }

        public T get() {
            return entry;
        }
    }
}
