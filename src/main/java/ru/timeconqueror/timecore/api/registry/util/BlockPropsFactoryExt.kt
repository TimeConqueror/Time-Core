package ru.timeconqueror.timecore.api.registry.util

import net.minecraft.block.AbstractBlock

fun AbstractBlock.Properties.unbreakable() = BlockPropsFactory.unbreakable(this)

operator fun BlockPropsFactory.invoke(): AbstractBlock.Properties = this.create()