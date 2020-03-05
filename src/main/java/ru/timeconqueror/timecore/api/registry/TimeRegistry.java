package ru.timeconqueror.timecore.api.registry;

import net.minecraftforge.fml.ModLoadingContext;
import ru.timeconqueror.timecore.api.TimeMod;

public abstract class TimeRegistry {
    private final TimeMod mod;

    public TimeRegistry() {
        Object mod = ModLoadingContext.get().getActiveContainer().getMod();
        if (!(mod instanceof TimeMod))
            throw new IllegalArgumentException("To use Auto Registries you must extend your main mod class from " + TimeMod.class);

        this.mod = ((TimeMod) mod);
    }

    /**
     * Method, where you should do all register stuff.
     */
    protected abstract void register();

    public TimeMod getMod() {
        return mod;
    }

    public String getModID() {
        return getMod().getModID();
    }
}
