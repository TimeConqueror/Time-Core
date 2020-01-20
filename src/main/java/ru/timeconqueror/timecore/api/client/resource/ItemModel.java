package ru.timeconqueror.timecore.api.client.resource;

import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;

public class ItemModel extends JSONTimeResource {
    /**
     * Represents the location of the model parent.
     * You should provide it in the constructor without <b>{@code 'models/'}</b> part and <b>file extension</b> in {@code path} param.<p>
     * Example: given parent resource location - {@code new ResourceLocation("minecraft", "item/handheld_rod");}<p>
     * Real location from given: {@code minecraft:models/item/handheld_rod.json}
     */
    private ResourceLocation parent;
    private ArrayList<ResourceLocation> layers = new ArrayList<>(1);

    public ItemModel(StandardItemModelParents parent) {
        this(parent.getResourceLocation());
    }

    public ItemModel(ResourceLocation parent) {
        this.parent = parent;
    }

    @Override
    public String buildJSONString() {
        return object(null, listOf(
                value("parent", parent.toString()),
                object("textures", listOf(() -> {
                            String[] jsonLayers = new String[this.layers.size()];
                            for (int i = 0; i < layers.size(); i++) {
                                jsonLayers[i] = value("layer" + i, layers.get(i).toString());
                            }
                            return jsonLayers;
                        })
                )
        ));
    }

    /**
     * Adds texture layer to the model.
     * <p>
     * Commonly you will need to provide only one texture to the model,
     * but sometimes you will need to set model to use combination of several textures.
     * Vanilla uses it in, for example, spawn egg model where the layers are represented by base texture and overlay (spots).
     */
    public ItemModel addTextureLayer(ResourceLocation textureLocation) {
        layers.add(textureLocation);

        return this;
    }

    /**
     * Adds texture layer to the model.
     * <p>
     * Commonly you will need to provide only one texture to the model,
     * but sometimes you will need to set model to use combination of several textures.
     * Vanilla uses it in, for example, spawn egg model where the layers are represented by base texture and overlay (spots).
     */
    public ItemModel addTextureLayers(ResourceLocation... textureLocations) {
        layers.addAll(Arrays.asList(textureLocations));

        return this;
    }
}
