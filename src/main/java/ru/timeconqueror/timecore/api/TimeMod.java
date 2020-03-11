package ru.timeconqueror.timecore.api;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModLoadingContext;

/**
 * Base mod class needed for TimeCore features.
 * <p>
 * Your main mod class should extend this if you want to apply such features as auto-registry.
 */
public abstract class TimeMod {
    private final String modID;

    public TimeMod() {
        modID = ModLoadingContext.get().getActiveNamespace();
    }

    /**
     * Returns mod id of this mod instance.
     */
    public String getModID() {
        return modID;
    }

    /**
     * Creates ResourceLocation with bound mod id.
     */
    public ResourceLocation createRl(String path) {
        return new ResourceLocation(modID, path);
    }
}
