package ru.timeconqueror.timecore.api.common.config

import net.minecraftforge.common.ForgeConfigSpec

fun <T> ImprovedConfigBuilder.optimized(configValueSup: ImprovedConfigBuilder.() -> ForgeConfigSpec.ConfigValue<T>): IQuickConfigValue<T> {
    return this.optimized(configValueSup(this))
}

fun <T, M> ImprovedConfigBuilder.optimized(
    configValueSup: ImprovedConfigBuilder.() -> ForgeConfigSpec.ConfigValue<T>,
    forwardMapper: (T) -> (M),
    backwardMapper: (M) -> (T)
): IQuickConfigValue<M> {
    return this.optimized(configValueSup(this), forwardMapper, backwardMapper)
}