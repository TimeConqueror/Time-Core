package ru.timeconqueror.timecore.api.registry.util

import net.minecraft.block.Block
import ru.timeconqueror.timecore.api.client.resource.BlockStateResource
import ru.timeconqueror.timecore.api.client.resource.location.BlockModelLocation
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation
import ru.timeconqueror.timecore.api.registry.BlockRegister
import ru.timeconqueror.timecore.api.registry.BlockRegister.BlockRegisterChain

class BlockRegisterContext(val register: BlockRegister) {
    infix fun <B : Block> String.represents(entrySupplier: () -> B): BlockChainContext<B> {
        val chain = register.register(this, entrySupplier)
        return BlockChainContext(chain)
    }

    infix fun <B : Block> BlockChainContext<B>.with(settings: BlockRegisterChain<B>.() -> Unit) {
        chain.also { settings(it) }
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

operator fun BlockRegister.invoke(block: BlockRegisterContext.() -> Unit) {
    block(BlockRegisterContext(this))
}

val BlockRegisterChain<*>.defaultBml: BlockModelLocation get() = RegisterUtils.defaultBml(this)
val BlockRegisterChain<*>.defaultBlockTl: TextureLocation get() = RegisterUtils.defaultBlockTl(this)
val BlockRegisterChain<*>.defaultItemTl: TextureLocation get() = RegisterUtils.defaultItemTl(this)