package ru.timeconqueror.timecore.registry;

import net.minecraftforge.fml.ModLoadingContext;
import ru.timeconqueror.timecore.api.TimeMod;

public abstract class TimeRegistry {

    public TimeRegistry() {
        Object mod = ModLoadingContext.get().getActiveContainer().getMod();
        if (!(mod instanceof TimeMod))
            throw new IllegalArgumentException("To use Auto Registries you must extend your main mod class from " + TimeMod.class);
    }

    public static String getModID() {
        return ModLoadingContext.get().getActiveNamespace();
    }

    /**
     * Method, where you should do all register stuff.
     */
    protected abstract void register();
}
