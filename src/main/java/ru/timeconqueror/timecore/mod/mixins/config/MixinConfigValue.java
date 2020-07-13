package ru.timeconqueror.timecore.mod.mixins.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.timeconqueror.timecore.api.common.config.IConfigValueEditable;

@Mixin(ForgeConfigSpec.ValueSpec.class)
@Implements({@Interface(iface = IConfigValueEditable.class, prefix = "i$")})
public abstract class MixinConfigValue {

    @Shadow
    private String langKey;

    @Shadow
    private String comment;

    public void i$addLineToComment(String addition) {
        comment += '\n' + addition;
    }


    public void i$setLangKey(String key) {
        this.langKey = key;
    }
}
