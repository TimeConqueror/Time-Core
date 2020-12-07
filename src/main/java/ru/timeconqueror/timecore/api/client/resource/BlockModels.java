package ru.timeconqueror.timecore.api.client.resource;

import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;

import static ru.timeconqueror.timecore.api.client.resource.JSONTimeResource.*;

public class BlockModels {
    /**
     * Creates block model with one provided texture for all sides.
     *
     * @see TextureLocation
     */
    public static BlockModel cubeAllModel(TextureLocation texture) {
        String json = object(null, listOf(
                property("parent", "block/cube_all"),
                object("textures", listOf(
                        property("all", texture.toString())
                ))
        ));
        return new BlockModel(json);
    }

    /**
     * Creates block model with one texture for top, one for bottom, and one for others.
     *
     * @param textureTop    texture that will be applied to the top side.
     * @param textureSide   texture that will be applied to the north, west, south, east sides.
     * @param textureBottom texture that will be applied to the bottom side.
     * @see TextureLocation
     */
    public static BlockModel cubeBottomTopModel(TextureLocation textureTop, TextureLocation textureSide, TextureLocation textureBottom) {
        String json = object(null, listOf(
                property("parent", "block/cube_bottom_top"),
                object("textures", listOf(
                        property("top", textureTop.toString()),
                        property("side", textureSide.toString()),
                        property("bottom", textureBottom.toString())
                ))
        ));
        return new BlockModel(json);
    }

    /**
     * Creates block model with one texture for top, and one for others.
     *
     * @param textureTop           texture that will be applied to the top side.
     * @param textureSideAndBottom texture that will be applied to the north, west, south, east and bottom sides.
     * @see TextureLocation
     */
    public static BlockModel cubeTopModel(TextureLocation textureTop, TextureLocation textureSideAndBottom) {
        String json = object(null, listOf(
                property("parent", "block/cube_top"),
                object("textures", listOf(
                        property("top", textureTop.toString()),
                        property("side", textureSideAndBottom.toString())
                ))
        ));
        return new BlockModel(json);
    }

    /**
     * Creates block model with one texture for top and bottom, and one for others.
     *
     * @param textureTopAndBottom texture that will be applied to the top and bottom sides.
     * @param textureSide         texture that will be applied to the north, west, south, east sides.
     * @see TextureLocation
     */
    public static BlockModel cubeColumnModel(TextureLocation textureTopAndBottom, TextureLocation textureSide) {
        String json = object(null, listOf(
                property("parent", "block/cube_column"),
                object("textures", listOf(
                        property("end", textureTopAndBottom.toString()),
                        property("side", textureSide.toString())
                ))
        ));
        return new BlockModel(json);
    }

    /**
     * Creates block model with one texture that will be seen like in grass, death bush models, etc.
     *
     * @see TextureLocation
     */
    public static BlockModel crossModel(TextureLocation textureCross) {
        String json = object(null, listOf(
                property("parent", "block/cross"),
                object("textures", listOf(
                        property("cross", textureCross.toString())
                ))
        ));
        return new BlockModel(json);
    }

    public static BlockModel stairsModel(TextureLocation bottom, TextureLocation top, TextureLocation side) {
        return stairsModel("block/stairs", bottom, top, side);
    }

    public static BlockModel stairsInnerModel(TextureLocation bottom, TextureLocation top, TextureLocation side) {
        return stairsModel("block/inner_stairs", bottom, top, side);
    }

    public static BlockModel stairsOuterModel(TextureLocation bottom, TextureLocation top, TextureLocation side) {
        return stairsModel("block/outer_stairs", bottom, top, side);
    }

    private static BlockModel stairsModel(String parent, TextureLocation bottom, TextureLocation top, TextureLocation side) {
        return new BlockModel(
                object(null, listOf(
                        property("parent", parent),
                        object("textures", listOf(
                                property("bottom", bottom.toString()),
                                property("top", top.toString()),
                                property("side", side.toString())
                        ))
                ))
        );
    }
}
