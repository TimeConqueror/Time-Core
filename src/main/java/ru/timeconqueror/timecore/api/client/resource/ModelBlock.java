package ru.timeconqueror.timecore.api.client.resource;

import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;

public class ModelBlock extends JSONTimeResource {
    private String jsonString;

    private ModelBlock(String jsonString) {
        this.jsonString = jsonString;
    }

    public static ModelBlock createCubeAllModel(TextureLocation texture) {
        String json = object(null, listOf(
                value("parent", "block/cube_all"),
                object("textures", listOf(
                        value("all", texture.toString())
                ))
        ));
        return new ModelBlock(json);
    }

    public static ModelBlock createCubeBottomTopModel(TextureLocation textureTop, TextureLocation textureSide, TextureLocation textureBottom) {
        String json = object(null, listOf(
                value("parent", "block/cube_bottom_top"),
                object("textures", listOf(
                        value("top", textureTop.toString()),
                        value("side", textureSide.toString()),
                        value("bottom", textureBottom.toString())
                ))
        ));
        return new ModelBlock(json);
    }

    public static ModelBlock createCubeTopModel(TextureLocation textureTop, TextureLocation textureSideAndBottom) {
        String json = object(null, listOf(
                value("parent", "block/cube_top"),
                object("textures", listOf(
                        value("top", textureTop.toString()),
                        value("side", textureSideAndBottom.toString())
                ))
        ));
        return new ModelBlock(json);
    }

    public static ModelBlock createCubeColumnModel(TextureLocation textureTopAndBottom, TextureLocation textureSide) {
        String json = object(null, listOf(
                value("parent", "block/cube_column"),
                object("textures", listOf(
                        value("end", textureTopAndBottom.toString()),
                        value("side", textureSide.toString())
                ))
        ));
        return new ModelBlock(json);
    }

    public static ModelBlock createCrossModel(TextureLocation textureCross) {
        String json = object(null, listOf(
                value("parent", "block/cross"),
                object("textures", listOf(
                        value("cross", textureCross.toString())
                ))
        ));
        return new ModelBlock(json);
    }

    @Override
    public String buildJSONString() {
        return jsonString;
    }
}