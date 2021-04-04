package ru.timeconqueror.timecore.common.capability.property.serializer

import net.minecraft.nbt.CompoundNBT


object LongPropertySerializer : IPropertySerializer<Long> {

    override fun serialize(name: String, value: Long, nbt: CompoundNBT) = nbt.putLong(name, value)
    override fun deserialize(name: String, nbt: CompoundNBT) = nbt.getLong(name)

    object Nullable : NullPropertySerializer<Long>(this)
}