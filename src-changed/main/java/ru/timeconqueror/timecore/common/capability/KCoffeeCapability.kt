package ru.timeconqueror.timecore.common.capability

import net.minecraftforge.common.capabilities.ICapabilityProvider
import ru.timeconqueror.timecore.common.capability.property.CoffeeProperty
import kotlin.reflect.KProperty0

abstract class KCoffeeCapability<T : ICapabilityProvider> : CoffeeCapability<T>() {
    fun <R> KProperty0<R>.markChanged(): R {
        if (this.getDelegate() is CoffeeProperty<*>) {
            (this.getDelegate() as CoffeeProperty<*>).changed = true
        }
        return this()
    }
}




