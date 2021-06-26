package ru.timeconqueror.timecore.mixins.accessor;

import net.minecraftforge.common.ForgeConfigSpec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ForgeConfigSpec.ValueSpec.class, remap = false)
public interface ValueSpecAccessor {
    @Accessor
    void setComment(String comment);

    @Accessor
    void setLangKey(String langKey);
}
