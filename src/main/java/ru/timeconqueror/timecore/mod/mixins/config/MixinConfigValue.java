package ru.timeconqueror.timecore.mod.mixins.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.spongepowered.asm.mixin.*;
import ru.timeconqueror.timecore.api.common.config.IConfigValueEditable;

@Mixin(ForgeConfigSpec.ValueSpec.class)
@Implements({@Interface(iface = IConfigValueEditable.class, prefix = "i$")})
public abstract class MixinConfigValue {

    @Mutable
    @Final
    @Shadow
    private String langKey;

    @Mutable
    @Final
    @Shadow
    private String comment;

    public void i$addLineToComment(String addition) {
        comment += '\n' + addition;
    }

    public void i$setLangKey(String key) {
        this.langKey = key;
    }
}
