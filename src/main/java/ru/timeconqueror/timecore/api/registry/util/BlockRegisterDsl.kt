package ru.timeconqueror.timecore.api.registry.util

import net.minecraft.block.Block
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
}

class BlockChainContext<B : Block>(val chain: BlockRegisterChain<B>)

operator fun BlockRegister.invoke(block: BlockRegisterContext.() -> Unit) {
    block(BlockRegisterContext(this))
}