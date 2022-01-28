package ru.timeconqueror.timecore.mixins.accessor;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = CapabilityManager.class, remap = false)
public interface CapabilityManagerAccessor {
    @Invoker("get")
    <T> Capability<T> callGet(String realName, boolean registering);
}
