package ru.timeconqueror.timecore.common.capability.property.serializer

import net.minecraft.nbt.CompoundNBT
import net.minecraftforge.common.util.INBTSerializable


open class NBTPropertySerializer<T : INBTSerializable<CompoundNBT>>(val factory: () -> T) : IPropertySerializer<T> {

    override fun serialize(name: String, value: T, nbt: CompoundNBT) {
        nbt.put(name, value.serializeNBT())
    }

    override fun deserialize(name: String, nbt: CompoundNBT): T {
        val value = factory()
        value.deserializeNBT(nbt.getCompound(name))
        return value
    }

    open class Nullable<T : INBTSerializable<CompoundNBT>>(factory: () -> T) : NullPropertySerializer<T>(
        NBTPropertySerializer(factory)
    )
}