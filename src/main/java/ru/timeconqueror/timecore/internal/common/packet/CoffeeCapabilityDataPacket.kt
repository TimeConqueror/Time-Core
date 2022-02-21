package ru.timeconqueror.timecore.internal.common.packet

import net.minecraft.nbt.CompoundNBT
import net.minecraft.network.PacketBuffer
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.fml.network.NetworkEvent
import ru.timeconqueror.timecore.TimeCore
import ru.timeconqueror.timecore.api.common.packet.ITimePacketHandler
import ru.timeconqueror.timecore.api.common.tile.SerializationType
import ru.timeconqueror.timecore.common.capability.CoffeeCapability
import ru.timeconqueror.timecore.common.capability.property.CoffeeProperty
import java.util.function.Predicate

sealed class CoffeeCapabilityDataPacket(
    val capabilityName: String,
    val ownerData: CompoundNBT,
    val capabilityData: CompoundNBT
) {

    companion object {
        fun <T : ICapabilityProvider> create(
            world: World,
            owner: T,
            cap: CoffeeCapability<T>,
            capabilityData: CompoundNBT,
            clientSide: Boolean
        ): CoffeeCapabilityDataPacket {
            return create(clientSide, cap.getCapability().name, CompoundNBT().apply {
                cap.getOwnerSerializer().serializeOwner(world, owner, this)
            }, capabilityData)
        }

        private fun create(
            clientSide: Boolean,
            capabilityName: String,
            ownerData: CompoundNBT,
            capabilityData: CompoundNBT
        ): CoffeeCapabilityDataPacket {
            return if (clientSide) {
                C2SCoffeeCapabilityDataPacket(capabilityName, ownerData, capabilityData)
            } else {
                S2CCoffeeCapabilityDataPacket(capabilityName, ownerData, capabilityData)
            }
        }

        fun <T : ICapabilityProvider> create(
            world: World,
            owner: T,
            cap: CoffeeCapability<T>,
            clientSide: Boolean,
            syncPredicate: Predicate<CoffeeProperty<*>>
        ): CoffeeCapabilityDataPacket? {
            val nbt = CompoundNBT()
            return if (cap.serialize(syncPredicate, nbt, clientSide, SerializationType.SYNC)) {
                create(world, owner, cap, nbt, clientSide)
            } else null
        }

        private fun handlePacket(packet: CoffeeCapabilityDataPacket, world: World, sentFromClient: Boolean) {
            val capability = TimeCore.INSTANCE.capabilityManager.getAttachableCoffeeCapability(packet.capabilityName)
            if (capability != null) {
                val ownerSerializer = capability.owner.serializer
                val owner: ICapabilityProvider? = ownerSerializer.deserializeOwner(world, packet.ownerData)

                if (owner != null) {
                    val cap: LazyOptional<out CoffeeCapability<*>> = owner.getCapability(capability.capability, null)

                    cap.ifPresent {
                        it.deserialize(packet.capabilityData, sentFromClient)
                    }
                }
            }
        }
    }

    sealed class Handler<T : CoffeeCapabilityDataPacket> :
        ITimePacketHandler<T> {
        final override fun encode(dataMsg: T, buf: PacketBuffer) {
            buf.writeBoolean(dataMsg is C2SCoffeeCapabilityDataPacket)
            buf.writeUtf(dataMsg.capabilityName)
            buf.writeNbt(dataMsg.ownerData)
            buf.writeNbt(dataMsg.capabilityData)
        }

        final override fun decode(buf: PacketBuffer): T {
            val sentFromClient = buf.readBoolean()
            return create(sentFromClient, buf.readUtf(), buf.readNbt()!!, buf.readNbt()!!) as T
        }

        final override fun handle(msg: T, ctx: NetworkEvent.Context): Boolean {
            val sentFromClient = ctx.direction.receptionSide == LogicalSide.SERVER

            ctx.enqueueWork {
                handlePacket(msg, getWorld(ctx), sentFromClient)
            }

            return true
        }

        object ServerHandler : CoffeeCapabilityDataPacket.Handler<C2SCoffeeCapabilityDataPacket>()
        object ClientHandler : CoffeeCapabilityDataPacket.Handler<S2CCoffeeCapabilityDataPacket>()
    }
}

class C2SCoffeeCapabilityDataPacket(capabilityName: String, ownerData: CompoundNBT, capabilityData: CompoundNBT) :
    CoffeeCapabilityDataPacket(capabilityName, ownerData, capabilityData)

class S2CCoffeeCapabilityDataPacket(capabilityName: String, ownerData: CompoundNBT, capabilityData: CompoundNBT) :
    CoffeeCapabilityDataPacket(capabilityName, ownerData, capabilityData)