package ru.timeconqueror.timecore.common.capability


import com.google.common.base.Predicates
import net.minecraft.nbt.CompoundNBT
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.INBTSerializable
import ru.timeconqueror.timecore.common.capability.property.CoffeeProperty
import ru.timeconqueror.timecore.common.capability.property.serializer.*
import kotlin.reflect.KProperty0

abstract class CoffeeCapability<T : ICapabilityProvider> : INBTSerializable<CompoundNBT>, ICoffeeCapability<T> {

    private val properties = ArrayList<CoffeeProperty<*>>()

    override fun getProperties() = properties

    protected fun <T> prop(name: String, value: T, serializer: IPropertySerializer<T>): CoffeeProperty<T> {
        val prop = CoffeeProperty(name, value, serializer)
        properties.add(prop)
        return prop
    }

    override fun serializeNBT(): CompoundNBT {
        val nbt = CompoundNBT()
        this.serializeProperties(Predicates.alwaysTrue(), nbt, false)
        return nbt
    }

    override fun deserializeNBT(nbt: CompoundNBT) {
        this.deserializeProperties(nbt)
    }

    protected fun prop(name: String, value: Int) = prop(name, value, IntPropertySerializer)
    protected fun prop(name: String, value: Long) = prop(name, value, LongPropertySerializer)
    protected fun prop(name: String, value: Float) = prop(name, value, FloatPropertySerializer)
    protected fun prop(name: String, value: Double) = prop(name, value, DoublePropertySerializer)
    protected fun prop(name: String, value: Boolean) = prop(name, value, BooleanPropertySerializer)
    protected fun prop(name: String, value: String) = prop(name, value, StringPropertySerializer)

    protected fun nullIntProp(name: String, value: Int? = null) = prop(name, value, IntPropertySerializer.Nullable)
    protected fun nullLongProp(name: String, value: Long? = null) = prop(name, value, LongPropertySerializer.Nullable)
    protected fun nullFloatProp(name: String, value: Float? = null) =
        prop(name, value, FloatPropertySerializer.Nullable)

    protected fun nullDoubleProp(name: String, value: Double? = null) =
        prop(name, value, DoublePropertySerializer.Nullable)

    protected fun nullBoolProp(name: String, value: Boolean? = null) =
        prop(name, value, BooleanPropertySerializer.Nullable)

    protected fun nullStringProp(name: String, value: String? = null) =
        prop(name, value, StringPropertySerializer.Nullable)

    fun <R> KProperty0<R>.markChanged(): R {
        if (this.getDelegate() is CoffeeProperty<*>) {
            (this.getDelegate() as CoffeeProperty<*>).changed = true
        }
        return this()
    }

}




