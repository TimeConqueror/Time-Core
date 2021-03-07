package ru.timeconqueror.timecore.api.client.resource;

import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.client.resource.InternalBlockModels;

public class BlockModels {
    /**
     * Creates block model with one texture for top & bottom, one for front, and one for side.
     *
     * @param topBottomTexture texture that will be applied to the top & bottom side.
     * @param frontTexture     texture that will be applied to the front side.
     * @param sideTexture      texture that will be applied to the all horizontal sides except front one.
     * @see TextureLocation
     */
    public static BlockModel cubeOrientableModel(TextureLocation topBottomTexture, TextureLocation frontTexture, TextureLocation sideTexture) {
        return InternalBlockModels.INSTANCE.cubeOrientable(topBottomTexture, frontTexture, sideTexture);
    }

    /**
     * Creates block model with one provided texture for all sides.
     *
     * @see TextureLocation
     */
    public static BlockModel cubeAllModel(TextureLocation texture) {
        return InternalBlockModels.INSTANCE.cubeAll(texture);
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
        return InternalBlockModels.INSTANCE.cubeBottomTop(textureTop, textureSide, textureBottom);
    }

    /**
     * Creates block model with one texture for top, and one for others.
     *
     * @param textureTop           texture that will be applied to the top side.
     * @param textureSideAndBottom texture that will be applied to the north, west, south, east and bottom sides.
     * @see TextureLocation
     */
    public static BlockModel cubeTopModel(TextureLocation textureTop, TextureLocation textureSideAndBottom) {
        return InternalBlockModels.INSTANCE.cubeTop(textureTop, textureSideAndBottom);
    }

    /**
     * Creates block model with one texture for top and bottom, and one for others.
     *
     * @param textureTopAndBottom texture that will be applied to the top and bottom sides.
     * @param textureSide         texture that will be applied to the north, west, south, east sides.
     * @see TextureLocation
     */
    public static BlockModel cubeColumnModel(TextureLocation textureTopAndBottom, TextureLocation textureSide) {
        return InternalBlockModels.INSTANCE.cubeColumn(textureTopAndBottom, textureSide);
    }

    /**
     * Creates block model with one texture that will be seen like in grass, death bush models, etc.
     *
     * @see TextureLocation
     */
    public static BlockModel crossModel(TextureLocation textureCross) {
        return InternalBlockModels.INSTANCE.cross(textureCross);
    }

    public static BlockModel stairsModel(TextureLocation bottom, TextureLocation top, TextureLocation side) {
        return InternalBlockModels.INSTANCE.stairs(bottom, top, side);
    }

    public static BlockModel stairsInnerModel(TextureLocation bottom, TextureLocation top, TextureLocation side) {
        return InternalBlockModels.INSTANCE.stairsInner(bottom, top, side);
    }

    public static BlockModel stairsOuterModel(TextureLocation bottom, TextureLocation top, TextureLocation side) {
        return InternalBlockModels.INSTANCE.stairsOuter(bottom, top, side);
    }

    public static BlockModel empty() {
        return InternalBlockModels.INSTANCE.empty();
    }
}
