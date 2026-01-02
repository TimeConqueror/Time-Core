package ru.timeconqueror.timecore.common.capability.property.serializer

import net.minecraft.nbt.CompoundTag
import ru.timeconqueror.timecore.api.util.INBTSimpleSerializable


open class NBTPropertySerializer<T : INBTSimpleSerializable<CompoundTag>>(val factory: () -> T) : IPropertySerializer<T> {

    override fun serialize(name: String, value: T, nbt: CompoundTag) {
        nbt.put(name, value.serializeNBT())
    }

    override fun deserialize(name: String, nbt: CompoundTag): T {
        val value = factory()
        value.deserializeNBT(nbt.getCompound(name))
        return value
    }

    open class Nullable<T : INBTSimpleSerializable<CompoundTag>>(factory: () -> T) : NullPropertySerializer<T>(
        NBTPropertySerializer(factory)
    )
}