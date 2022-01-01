package ru.timeconqueror.timecore.common.capability.property.serializer

import net.minecraft.nbt.CompoundTag


object LongPropertySerializer : IPropertySerializer<Long> {

    override fun serialize(name: String, value: Long, nbt: CompoundTag) = nbt.putLong(name, value)
    override fun deserialize(name: String, nbt: CompoundTag) = nbt.getLong(name)

    object Nullable : NullPropertySerializer<Long>(this)
}