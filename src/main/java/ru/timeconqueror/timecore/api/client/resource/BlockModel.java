package ru.timeconqueror.timecore.api.client.resource;

import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;

/**
 * Code analog to simple block model json files.
 * Contains some static methods for its creating.
 */
public class BlockModel extends JSONTimeResource {
    private final String jsonString;

    public BlockModel(String jsonString) {
        this.jsonString = jsonString;
    }

    /**
     * Creates block model with one provided texture for all sides.
     *
     * @see TextureLocation
     */
    public static BlockModel createCubeAllModel(TextureLocation texture) {
        String json = object(null, listOf(
                value("parent", "block/cube_all"),
                object("textures", listOf(
                        value("all", texture.toString())
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
    public static BlockModel createCubeBottomTopModel(TextureLocation textureTop, TextureLocation textureSide, TextureLocation textureBottom) {
        String json = object(null, listOf(
                value("parent", "block/cube_bottom_top"),
                object("textures", listOf(
                        value("top", textureTop.toString()),
                        value("side", textureSide.toString()),
                        value("bottom", textureBottom.toString())
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
    public static BlockModel createCubeTopModel(TextureLocation textureTop, TextureLocation textureSideAndBottom) {
        String json = object(null, listOf(
                value("parent", "block/cube_top"),
                object("textures", listOf(
                        value("top", textureTop.toString()),
                        value("side", textureSideAndBottom.toString())
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
    public static BlockModel createCubeColumnModel(TextureLocation textureTopAndBottom, TextureLocation textureSide) {
        String json = object(null, listOf(
                value("parent", "block/cube_column"),
                object("textures", listOf(
                        value("end", textureTopAndBottom.toString()),
                        value("side", textureSide.toString())
                ))
        ));
        return new BlockModel(json);
    }

    /**
     * Creates block model with one texture that will be seen like in grass, death bush models, etc.
     *
     * @see TextureLocation
     */
    public static BlockModel createCrossModel(TextureLocation textureCross) {
        String json = object(null, listOf(
                value("parent", "block/cross"),
                object("textures", listOf(
                        value("cross", textureCross.toString())
                ))
        ));
        return new BlockModel(json);
    }

    @Override
    public String buildJSONString() {
        return jsonString;
    }
}