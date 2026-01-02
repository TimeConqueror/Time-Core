package ru.timeconqueror.timecore.api.common.config

import net.neoforged.neoforge.common.ModConfigSpec


fun <T> ImprovedConfigBuilder.optimized(configValueSup: ImprovedConfigBuilder.() -> ModConfigSpec.ConfigValue<T>): IQuickConfigValue<T> {
    return this.optimized(configValueSup(this))
}

fun <T, M> ImprovedConfigBuilder.optimized(
    configValueSup: ImprovedConfigBuilder.() -> ModConfigSpec.ConfigValue<T>,
    forwardMapper: (T) -> (M),
    backwardMapper: (M) -> (T)
): IQuickConfigValue<M> {
    return this.optimized(configValueSup(this), forwardMapper, backwardMapper)
}