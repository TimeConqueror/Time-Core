package ru.timeconqueror.timecore.api.registry.util

import net.minecraft.world.level.block.Block
import net.minecraftforge.registries.RegistryObject
import ru.timeconqueror.timecore.api.client.resource.BlockStateResource
import ru.timeconqueror.timecore.api.client.resource.location.BlockModelLocation
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation
import ru.timeconqueror.timecore.api.registry.BlockRegister
import ru.timeconqueror.timecore.api.registry.BlockRegister.BlockRegisterChain

operator fun BlockRegister.invoke(block: BlockRegisterContext<Block>.() -> Unit) {
    block(BlockRegisterContext(this))
}

class BlockRegisterContext<B : Block>(val register: BlockRegister, private val chainConfigurator: ChainConfigurator<B>? = null) {
    fun <E : B> groupSettings(groupConfigurator: ChainConfigurator<E>): BlockGroupContext<E> {
        return BlockGroupContext(register, groupConfigurator)
    }

    infix fun <E : B> String.represents(entrySupplier: () -> E): BlockChainContext<E> {
        val chain = register.register(this, entrySupplier)
        return BlockChainContext(chain)
    }

    infix fun <E : B> BlockChainContext<E>.with(settings: ChainConfigurator<E>): RegistryObject<E> {
        chainConfigurator?.invoke(chain)
        chain.also { settings(it) }
        return chain.asRegistryObject()
    }

    var BlockRegisterChain<*>.state: BlockStateResource
        get() = throw UnsupportedOperationException()
        set(value) {
            state(value)
        }
    var BlockRegisterChain<*>.name: String
        get() = throw UnsupportedOperationException()
        set(value) {
            name(value)
        }

    /**
     * Returns block texture location with bound modid from entry.
     */
    fun BlockRegisterChain<*>.blockTl(path: String) = TextureLocation(modId, "block/$path")

    /**
     * Returns item texture location with bound modid from entry.
     */
    fun BlockRegisterChain<*>.itemTl(path: String) = TextureLocation(modId, "item/$path")

    /**
     * Returns block model location with bound modid from entry.
     */
    fun BlockRegisterChain<*>.bml(path: String) = BlockModelLocation(modId, path)
}

class BlockChainContext<B : Block>(val chain: BlockRegisterChain<B>)
typealias ChainConfigurator<B> = BlockRegisterChain<out B>.() -> Unit

class BlockGroupContext<B : Block>(private val register: BlockRegister, private val chainConfigurator: ChainConfigurator<B>) {
    infix fun applyFor(blockRegistrator: BlockRegisterContext<B>.() -> Unit) {
        val blockRegisterContext = BlockRegisterContext(register, this.chainConfigurator)
        blockRegistrator.invoke(blockRegisterContext)
    }
}

val BlockRegisterChain<*>.defaultBml: BlockModelLocation get() = RegisterUtils.defaultBml(this)
val BlockRegisterChain<*>.defaultBlockTl: TextureLocation get() = RegisterUtils.defaultBlockTl(this)
val BlockRegisterChain<*>.defaultItemTl: TextureLocation get() = RegisterUtils.defaultItemTl(this)