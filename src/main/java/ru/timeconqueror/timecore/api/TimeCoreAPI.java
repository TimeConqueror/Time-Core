package ru.timeconqueror.timecore.api;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforgespi.language.IModInfo;
import net.neoforged.neoforgespi.language.ModFileScanData;
import ru.timeconqueror.timecore.internal.loading.ModInitializer;

public class TimeCoreAPI {
    /**
     * Sets up TimeCore components for your mod.
     * Should be called once in the constructor of your mod.
     * In most cases it will look like this:
     * <pre>TimeCoreAPI.setup(this)</pre>
     *
     * @param modInstance - instance of the mod to be set up for TimeCore components.
     */
    public static void setup(Object modInstance) {
        ModLoadingContext modLoadingCtx = ModLoadingContext.get();
        ModContainer container = modLoadingCtx.getActiveContainer();
        IEventBus eventBus = container.getEventBus();

        if(eventBus == null) {
            throw new IllegalStateException("Mod %s is not the common one, because it doesn't have mod event bus to be used".formatted(container.getModId()));
        }

        IModInfo modInfo = container.getModInfo();
        ModFileScanData scanResult = modInfo.getOwningFile().getFile().getScanResult();
        ModInitializer.run(eventBus, container, scanResult, modInstance);
    }
}
