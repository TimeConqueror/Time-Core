@file:JvmName("MessageUtils")
@file:JvmMultifileClass

package ru.timeconqueror.timecore.api.auxiliary

import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextFormatting

fun <T : ITextComponent> T.color(color: TextFormatting): T {
    this.style.color = color
    return this
}