package ru.timeconqueror.timecore.client.resource

import ru.timeconqueror.timecore.api.client.resource.BlockStateResource
import ru.timeconqueror.timecore.api.client.resource.location.BlockModelLocation
import ru.timeconqueror.timecore.api.util.json.json

object KtBlockStateResourceFactory {
    @JvmStatic
    fun singleVariantWithSingleModel(model: BlockModelLocation): BlockStateResource {
        return BlockStateResource.fromJson(json {
            "variants" {
                "" {
                    "model" set model
                }
            }
        })
    }
}
