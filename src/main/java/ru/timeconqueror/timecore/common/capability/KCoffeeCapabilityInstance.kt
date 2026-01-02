//package ru.timeconqueror.timecore.common.capability
//
//import net.minecraftforge.common.capabilities.ICapabilityProvider
//import ru.timeconqueror.timecore.common.capability.owner.CapabilityOwnerType
//import ru.timeconqueror.timecore.common.capability.property.CoffeeProperty
//import kotlin.reflect.KProperty0
//
//abstract class KCoffeeCapabilityInstance<T : ICapabilityProvider>(owner: CapabilityOwnerType<in T>) :
//    CoffeeCapabilityInstance<T>(
//        owner
//    ) {
//    fun <R> KProperty0<R>.markChanged(): R {
//        if (this.getDelegate() is CoffeeProperty<*>) {
//            (this.getDelegate() as CoffeeProperty<*>).changed = true
//        }
//        return this()
//    }
//}
//
//
//
//
