package ru.timeconqueror.timecore.api.client.resource.blockstates

import ru.timeconqueror.timecore.api.client.resource.BlockStateResource
import ru.timeconqueror.timecore.api.client.resource.location.BlockModelLocation
import ru.timeconqueror.timecore.api.util.json.*

object StairsBlockStateFactory {
    @JvmStatic
    fun create(stairs: BlockModelLocation, innerStairs: BlockModelLocation, outerStairs: BlockModelLocation): BlockStateResource =
        json {
            "variants" {
                "facing=east,half=bottom,shape=inner_left" {
                    set model innerStairs
                    y = 270
                    uvlock
                }
                "facing=east,half=bottom,shape=inner_right" {
                    set model innerStairs
                }
                "facing=east,half=bottom,shape=outer_left" {
                    set model outerStairs
                    y = 270
                    uvlock
                }
                "facing=east,half=bottom,shape=outer_right" {
                    set model outerStairs
                }
                "facing=east,half=bottom,shape=straight" {
                    set model stairs
                }
                "facing=east,half=top,shape=inner_left" {
                    set model innerStairs
                    x = 180
                    uvlock
                }
                "facing=east,half=top,shape=inner_right" {
                    set model innerStairs
                    x = 180
                    y = 90
                    uvlock
                }
                "facing=east,half=top,shape=outer_left" {
                    set model outerStairs
                    x = 180
                    uvlock
                }
                "facing=east,half=top,shape=outer_right" {
                    set model outerStairs
                    x = 180
                    y = 90
                    uvlock
                }
                "facing=east,half=top,shape=straight" {
                    set model stairs
                    x = 180
                    uvlock
                }
                "facing=north,half=bottom,shape=inner_left" {
                    set model innerStairs
                    y = 180
                    uvlock
                }
                "facing=north,half=bottom,shape=inner_right" {
                    set model innerStairs
                    y = 270
                    uvlock
                }
                "facing=north,half=bottom,shape=outer_left" {
                    set model outerStairs
                    y = 180
                    uvlock
                }
                "facing=north,half=bottom,shape=outer_right" {
                    set model outerStairs
                    y = 270
                    uvlock
                }
                "facing=north,half=bottom,shape=straight" {
                    set model stairs
                    y = 270
                    uvlock
                }
                "facing=north,half=top,shape=inner_left" {
                    set model innerStairs
                    x = 180
                    y = 270
                    uvlock
                }
                "facing=north,half=top,shape=inner_right" {
                    set model innerStairs
                    x = 180
                    uvlock
                }
                "facing=north,half=top,shape=outer_left" {
                    set model outerStairs
                    x = 180
                    y = 270
                    uvlock
                }
                "facing=north,half=top,shape=outer_right" {
                    set model outerStairs
                    x = 180
                    uvlock
                }
                "facing=north,half=top,shape=straight" {
                    set model stairs
                    x = 180
                    y = 270
                    uvlock
                }
                "facing=south,half=bottom,shape=inner_left" {
                    set model innerStairs
                }
                "facing=south,half=bottom,shape=inner_right" {
                    set model innerStairs
                    y = 90
                    uvlock
                }
                "facing=south,half=bottom,shape=outer_left" {
                    set model outerStairs
                }
                "facing=south,half=bottom,shape=outer_right" {
                    set model outerStairs
                    y = 90
                    uvlock
                }
                "facing=south,half=bottom,shape=straight" {
                    set model stairs
                    y = 90
                    uvlock
                }
                "facing=south,half=top,shape=inner_left" {
                    set model innerStairs
                    x = 180
                    y = 90
                    uvlock
                }
                "facing=south,half=top,shape=inner_right" {
                    set model innerStairs
                    x = 180
                    y = 180
                    uvlock
                }
                "facing=south,half=top,shape=outer_left" {
                    set model outerStairs
                    x = 180
                    y = 90
                    uvlock
                }
                "facing=south,half=top,shape=outer_right" {
                    set model outerStairs
                    x = 180
                    y = 180
                    uvlock
                }
                "facing=south,half=top,shape=straight" {
                    set model stairs
                    x = 180
                    y = 90
                    uvlock
                }
                "facing=west,half=bottom,shape=inner_left" {
                    set model innerStairs
                    y = 90
                    uvlock
                }
                "facing=west,half=bottom,shape=inner_right" {
                    set model innerStairs
                    y = 180
                    uvlock
                }
                "facing=west,half=bottom,shape=outer_left" {
                    set model outerStairs
                    y = 90
                    uvlock
                }
                "facing=west,half=bottom,shape=outer_right" {
                    set model outerStairs
                    y = 180
                    uvlock
                }
                "facing=west,half=bottom,shape=straight" {
                    set model stairs
                    y = 180
                    uvlock
                }
                "facing=west,half=top,shape=inner_left" {
                    set model innerStairs
                    x = 180
                    y = 180
                    uvlock
                }
                "facing=west,half=top,shape=inner_right" {
                    set model innerStairs
                    x = 180
                    y = 270
                    uvlock
                }
                "facing=west,half=top,shape=outer_left" {
                    set model outerStairs
                    x = 180
                    y = 180
                    uvlock
                }
                "facing=west,half=top,shape=outer_right" {
                    set model outerStairs
                    x = 180
                    y = 270
                    uvlock
                }
                "facing=west,half=top,shape=straight" {
                    set model stairs
                    x = 180
                    y = 180
                    uvlock
                }
            }
        }.let(BlockStateResource::fromJson)
}