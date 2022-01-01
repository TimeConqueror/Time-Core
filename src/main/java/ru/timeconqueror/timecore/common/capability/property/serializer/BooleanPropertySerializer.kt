package ru.timeconqueror.timecore.common.capability.property.serializer

import net.minecraft.nbt.CompoundTag


object BooleanPropertySerializer : IPropertySerializer<Boolean> {

    override fun serialize(name: String, value: Boolean, nbt: CompoundTag) = nbt.putBoolean(name, value)
    override fun deserialize(name: String, nbt: CompoundTag) = nbt.getBoolean(name)

    object Nullable : NullPropertySerializer<Boolean>(this)
}