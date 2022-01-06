package ru.timeconqueror.timecore.api;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.ModFileScanData;
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

        if (container.getModId().equals("minecraft") || container.getCurrentState() != ModLoadingStage.CONSTRUCT) {
            throw new IllegalStateException("This method should be called only in mod constructor!");
        }

        IModInfo modInfo = container.getModInfo();
        ModFileScanData scanResult = modInfo.getOwningFile().getFile().getScanResult();
        ModInitializer.run(container, scanResult, modInstance);
    }
}
