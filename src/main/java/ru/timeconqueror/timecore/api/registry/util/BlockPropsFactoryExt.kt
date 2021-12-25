package ru.timeconqueror.timecore.api.registry.util

import net.minecraft.world.level.block.state.BlockBehaviour

fun BlockBehaviour.Properties.unbreakable() = BlockPropsFactory.unbreakable(this)

operator fun BlockPropsFactory.invoke(): BlockBehaviour.Properties = this.create()