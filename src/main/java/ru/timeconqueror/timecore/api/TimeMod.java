package ru.timeconqueror.timecore.api;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModLoadingContext;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;

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
    public ResourceLocation rl(String path) {
        return new ResourceLocation(modID, path);
    }

    public TextureLocation texture(String path) {
        return new TextureLocation(modID, path);
    }
}
