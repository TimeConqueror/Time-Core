package ru.timeconqueror.timecore.client.resource

import ru.timeconqueror.timecore.api.client.resource.BlockModel
import ru.timeconqueror.timecore.api.client.resource.BlockModels
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation
import ru.timeconqueror.timecore.api.util.json

object InternalBlockModels {
    /**
     * @see BlockModels.cubeOrientableModel
     */
    fun cubeOrientable(
        topBottomTexture: TextureLocation,
        frontTexture: TextureLocation,
        sideTexture: TextureLocation
    ) = BlockModel(json {
        "parent" set "block/orientable"
        "textures" {
            "top" set topBottomTexture.toString()
            "front" set frontTexture.toString()
            "side" set sideTexture.toString()
        }
    })

    /**
     * @see BlockModels.cubeAllModel
     */
    fun cubeAll(texture: TextureLocation) = BlockModel(json {
        "parent" set "block/cube_all"
        "textures" {
            "all" set texture.toString()
        }
    })

    /**
     * @see BlockModels.cubeBottomTopModel
     */
    fun cubeBottomTop(
        textureTop: TextureLocation,
        textureSide: TextureLocation,
        textureBottom: TextureLocation
    ) = BlockModel(json {
        "parent" set "block/cube_bottom_top"
        "textures" {
            "top" set textureTop.toString()
            "side" set textureSide.toString()
            "bottom" set textureBottom.toString()
        }
    })

    /**
     * @see BlockModels.cubeTopModel
     */
    fun cubeTop(textureTop: TextureLocation, textureSideAndBottom: TextureLocation) = BlockModel(json {
        "parent" set "block/cube_top"
        "textures" {
            "top" set textureTop.toString()
            "side" set textureSideAndBottom.toString()
        }
    })

    /**
     * @see BlockModels.cubeColumnModel
     */
    fun cubeColumn(textureTopAndBottom: TextureLocation, textureSide: TextureLocation) = BlockModel(json {
        "parent" set "block/cube_column"
        "textures" {
            "end" set textureTopAndBottom.toString()
            "side" set textureSide.toString()
        }
    })

    /**
     * @see BlockModels.crossModel
     */
    fun cross(textureCross: TextureLocation) = BlockModel(json {
        "parent" set "block/cross"
        "textures" {
            "cross" set textureCross.toString()
        }
    })

    /**
     * @see BlockModels.stairsModel
     */
    fun stairs(bottom: TextureLocation, top: TextureLocation, side: TextureLocation) =
        stairs("block/stairs", bottom, top, side)

    /**
     * @see BlockModels.stairsInnerModel
     */
    fun stairsInner(bottom: TextureLocation, top: TextureLocation, side: TextureLocation) =
        stairs("block/inner_stairs", bottom, top, side)

    /**
     * @see BlockModels.stairsOuterModel
     */
    fun stairsOuter(bottom: TextureLocation, top: TextureLocation, side: TextureLocation) =
        stairs("block/outer_stairs", bottom, top, side)

    private fun stairs(parent: String, bottom: TextureLocation, top: TextureLocation, side: TextureLocation) =
        BlockModel(json {
            "parent" set parent
            "textures" {
                "bottom" set bottom.toString()
                "top" set top.toString()
                "side" set side.toString()
            }
        })

    fun empty() = EMPTY

    private val EMPTY = BlockModel(json { })
}