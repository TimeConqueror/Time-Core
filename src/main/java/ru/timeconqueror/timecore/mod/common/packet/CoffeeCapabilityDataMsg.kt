package ru.timeconqueror.timecore.mod.common.packet

import net.minecraft.nbt.CompoundNBT
import net.minecraft.network.PacketBuffer
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.fml.network.NetworkEvent
import ru.timeconqueror.timecore.TimeCore
import ru.timeconqueror.timecore.api.common.packet.ITimePacket
import ru.timeconqueror.timecore.common.capability.ICoffeeCapability
import ru.timeconqueror.timecore.common.capability.property.CoffeeProperty
import java.util.function.Predicate

sealed class CoffeeCapabilityDataMsg(
    val capabilityName: String,
    val ownerData: CompoundNBT,
    val capabilityData: CompoundNBT
) :
    ITimePacket {

    companion object {
        fun <T : ICapabilityProvider> create(
            world: World,
            owner: T,
            cap: ICoffeeCapability<T>,
            capabilityData: CompoundNBT,
            clientSide: Boolean
        ): CoffeeCapabilityDataMsg {
            return create(clientSide, cap.getCapability().name, CompoundNBT().apply {
                cap.getOwnerSerializer().serializeOwner(world, owner, this)
            }, capabilityData)
        }

        private fun create(
            clientSide: Boolean,
            capabilityName: String,
            ownerData: CompoundNBT,
            capabilityData: CompoundNBT
        ): CoffeeCapabilityDataMsg {
            return if (clientSide) {
                C2SCoffeeCapabilityDataMsg(capabilityName, ownerData, capabilityData)
            } else {
                S2CCoffeeCapabilityDataMsg(capabilityName, ownerData, capabilityData)
            }
        }

        fun <T : ICapabilityProvider> create(
            world: World,
            owner: T,
            cap: ICoffeeCapability<T>,
            clientSide: Boolean,
            syncPredicate: Predicate<CoffeeProperty<*>>
        ): CoffeeCapabilityDataMsg? {
            val nbt = CompoundNBT()
            return if (cap.serializeProperties(syncPredicate, nbt, clientSide)) {
                create(world, owner, cap, nbt, clientSide)
            } else null
        }

        private fun handlePacket(msg: CoffeeCapabilityDataMsg, world: World, fromClient: Boolean) {
            val capability = TimeCore.INSTANCE.capabilityManager.getAttachableCoffeeCapability(msg.capabilityName)
            if (capability != null) {

                val ownerSerializer = capability.owner.serializer
                val owner: ICapabilityProvider? = ownerSerializer.deserializeOwner(world, msg.ownerData)

                if (owner != null) {
                    val cap: LazyOptional<out ICoffeeCapability<*>> = owner.getCapability(capability.capability, null)

                    cap.ifPresent {
                        it.deserializeProperties(msg.capabilityData, fromClient)
                    }
                }
            }
        }
    }

    abstract class Handler<T : CoffeeCapabilityDataMsg> : ITimePacket.ITimePacketHandler<T> {
        final override fun encode(dataMsg: T, buf: PacketBuffer) {
            buf.writeBoolean(dataMsg is C2SCoffeeCapabilityDataMsg)
            buf.writeUtf(dataMsg.capabilityName)
            buf.writeNbt(dataMsg.ownerData)
            buf.writeNbt(dataMsg.capabilityData)
        }

        final override fun decode(buf: PacketBuffer): T {
            val sentFromClient = buf.readBoolean()
            return create(sentFromClient, buf.readUtf(), buf.readNbt()!!, buf.readNbt()!!) as T
        }

        final override fun handle(msg: T, ctx: NetworkEvent.Context): Boolean {
            val fromClient = ctx.direction.receptionSide == LogicalSide.SERVER

            ctx.enqueueWork {
                handlePacket(msg, getWorld(ctx), fromClient)
            }

            return true
        }
    }
}

object ServerHandler : CoffeeCapabilityDataMsg.Handler<C2SCoffeeCapabilityDataMsg>()
object ClientHandler : CoffeeCapabilityDataMsg.Handler<S2CCoffeeCapabilityDataMsg>()

class C2SCoffeeCapabilityDataMsg(capabilityName: String, ownerData: CompoundNBT, capabilityData: CompoundNBT) :
    CoffeeCapabilityDataMsg(capabilityName, ownerData, capabilityData)

class S2CCoffeeCapabilityDataMsg(capabilityName: String, ownerData: CompoundNBT, capabilityData: CompoundNBT) :
    CoffeeCapabilityDataMsg(capabilityName, ownerData, capabilityData)