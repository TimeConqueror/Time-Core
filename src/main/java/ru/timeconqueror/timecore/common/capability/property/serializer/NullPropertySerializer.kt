package ru.timeconqueror.timecore.common.capability.property.serializer

import net.minecraft.nbt.CompoundTag


open class NullPropertySerializer<T>(val prop: IPropertySerializer<T>) : IPropertySerializer<T?> {

    override fun serialize(name: String, value: T?, nbt: CompoundTag) {
        val comp = CompoundTag()
        if (value != null) {
            prop.serialize("value", value, comp)
        }
        nbt.put(name, comp)
    }

    override fun deserialize(name: String, nbt: CompoundTag): T? {
        val comp = nbt.getCompound(name)
        if (comp.contains("value")) {
            return prop.deserialize("value", comp)
        }
        return null
    }


}