package ru.timeconqueror.timecore.api.devtools.gen.advancement

import net.minecraft.advancements.Advancement
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.common.data.ExistingFileHelper
import java.util.function.Consumer

fun saverAwareAdvancementProvider(
    modId: String,
    saveFunc: Consumer<Advancement>,
    fileHelper: ExistingFileHelper,
    block: SaverAwareAdvancementBuilderContext.() -> Unit
) {
    val ctx = SaverAwareAdvancementBuilderContext(modId, { saveFunc.accept(it) }, fileHelper)
    block(ctx)
}

class SaverAwareAdvancementBuilderContext(
    private val modId: String,
    private val saveFunc: (Advancement) -> Unit,
    private val fileHelper: ExistingFileHelper
) {
    fun make(id: String, block: Advancement.Builder.() -> Unit): Advancement {
        return make(ResourceLocation(modId, id), block)
    }

    fun make(id: ResourceLocation, block: Advancement.Builder.() -> Unit): Advancement {
        val builder = Advancement.Builder.advancement()
        block(builder)
        builder.save(saveFunc, id, fileHelper)
        return builder.build(id)
    }
}