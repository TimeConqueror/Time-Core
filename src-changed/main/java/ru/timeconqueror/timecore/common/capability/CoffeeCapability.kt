package ru.timeconqueror.timecore.common.capability

import net.minecraft.nbt.CompoundNBT
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.fml.network.simple.SimpleChannel
import ru.timeconqueror.timecore.api.common.tile.SerializationType
import ru.timeconqueror.timecore.common.capability.owner.serializer.CapabilityOwnerSerializer
import ru.timeconqueror.timecore.common.capability.property.CoffeeProperty
import ru.timeconqueror.timecore.common.capability.property.container.PropertyContainer
import ru.timeconqueror.timecore.mod.common.packet.CoffeeCapabilityDataPacket
import ru.timeconqueror.timecore.mod.common.packet.InternalPacketManager
import java.util.function.Predicate
import javax.annotation.ParametersAreNonnullByDefault

//TODO property enums,
//TODO custom properties,
//TODO move all extra attach (keep after death, etc) to properties
//TODO doNotSync, doNotSave
//ToDO check client dependent caps being saved on server?
//TODO store owner inside the cap so people don't bother creating their own sendAllData & sendChangesToClients without args
@ParametersAreNonnullByDefault
abstract class CoffeeCapability<T : ICapabilityProvider> : PropertyContainer(), INBTSerializable<CompoundNBT> {

    abstract fun getOwnerSerializer(): CapabilityOwnerSerializer<T>
    abstract fun getCapability(): Capability<out CoffeeCapability<T>>

    /**
     * Checks if properties of capability has changed and if yes, sends them.
     * Works in both directions.
     */
    fun detectAndSendChanges(world: World, owner: T) {
        sendData(world, owner) { prop ->
            if (prop.changed) {
                prop.changed = false
                true
            } else false
        }
    }

    /**
     * Synchronizes all capability data.
     * Works in both directions.
     */
    fun sendAllData(world: World, owner: T) {
        sendData(world, owner) { true }
    }

    abstract fun sendChangesToClients(channel: SimpleChannel, data: Any)

    private fun sendData(world: World, owner: T, predicate: Predicate<CoffeeProperty<*>>) {
        val clientSide = world.isClientSide()

        val message = createDataPacket(world, owner, clientSide, predicate)

        if (message != null) {
            if (clientSide) {
                InternalPacketManager.INSTANCE.sendToServer(message)
            } else {
                sendChangesToClients(InternalPacketManager.INSTANCE, message)
            }
        }
    }

    private fun createDataPacket(
        world: World,
        owner: T,
        clientSide: Boolean,
        syncPredicate: Predicate<CoffeeProperty<*>>
    ) = CoffeeCapabilityDataPacket.create(world, owner, this, clientSide, syncPredicate)

    override fun serializeNBT(): CompoundNBT {
        val compound = CompoundNBT()
        serialize({ true }, compound, false, SerializationType.SAVE)
        return compound
    }

    override fun deserializeNBT(nbt: CompoundNBT) {
        deserialize(nbt)
    }
}