package ru.timeconqueror.timecore.api.client.resource;

import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;

public class BlockModel extends JSONTimeResource {
    private String jsonString;

    private BlockModel(String jsonString) {
        this.jsonString = jsonString;
    }

    public static BlockModel createCubeAllModel(TextureLocation texture) {
        String json = object(null, listOf(
                value("parent", "block/cube_all"),
                object("textures", listOf(
                        value("all", texture.toString())
                ))
        ));
        return new BlockModel(json);
    }

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