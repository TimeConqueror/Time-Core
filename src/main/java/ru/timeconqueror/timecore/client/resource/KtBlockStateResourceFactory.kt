package ru.timeconqueror.timecore.client.resource

import ru.timeconqueror.timecore.api.client.resource.BlockStateResource
import ru.timeconqueror.timecore.api.client.resource.location.BlockModelLocation
import ru.timeconqueror.timecore.api.util.json.json
import ru.timeconqueror.timecore.api.util.json.model

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

    @JvmStatic
    fun slab(modelBottom: BlockModelLocation, modelDouble: BlockModelLocation, modelTop: BlockModelLocation): BlockStateResource {
        return BlockStateResource.fromJson(json {
            "variants" {
                "type=bottom" {
                    set model modelBottom
                }
                "type=double" {
                    set model modelDouble
                }
                "type=top" {
                    set model modelTop
                }
            }
        })
    }
}
