package ru.timeconqueror.timecore.api;

import net.minecraftforge.fml.ModLoadingContext;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;

/**
 * Base mod class needed for TimeCore features.
 * <p>
 * Your main mod class should extend this if you want to apply such features as auto-registry.
 */
public abstract class TimeMod {//TODO move to interface
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

    public TextureLocation texture(String path) {
        return new TextureLocation(modID, path);
    }
}
