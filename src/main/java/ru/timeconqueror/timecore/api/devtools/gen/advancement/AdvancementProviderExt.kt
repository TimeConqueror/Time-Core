package ru.timeconqueror.timecore.api.devtools.gen.advancement

import net.minecraft.advancements.Advancement
import net.minecraft.advancements.AdvancementHolder
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.common.data.ExistingFileHelper
import java.util.function.Consumer

fun saverAwareAdvancementProvider(
    modId: String,
    saveFunc: Consumer<AdvancementHolder>,
    fileHelper: ExistingFileHelper,
    block: SaverAwareAdvancementBuilderContext.() -> Unit
) {
    val ctx = SaverAwareAdvancementBuilderContext(modId, { saveFunc.accept(it) }, fileHelper)
    block(ctx)
}

class SaverAwareAdvancementBuilderContext(
    private val modId: String,
    private val saveFunc: (AdvancementHolder) -> Unit,
    private val fileHelper: ExistingFileHelper
) {
    fun make(id: String, block: Advancement.Builder.() -> Unit): AdvancementHolder {
        return make(ResourceLocation.fromNamespaceAndPath(modId, id), block)
    }

    fun make(id: ResourceLocation, block: Advancement.Builder.() -> Unit): AdvancementHolder {
        val builder = Advancement.Builder.advancement()
        block(builder)
        builder.save(saveFunc, id, fileHelper)
        return builder.build(id)
    }
}