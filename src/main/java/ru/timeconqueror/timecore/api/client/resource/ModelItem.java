package ru.timeconqueror.timecore.api.client.resource;

import ru.timeconqueror.timecore.api.client.resource.location.ModelLocation;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;

import java.util.ArrayList;
import java.util.Arrays;

public class ModelItem extends JSONTimeResource {
    /**
     * Represents the location of the model parent.
     */
    private ModelLocation parent;
    private ArrayList<TextureLocation> layers = new ArrayList<>(1);

    public ModelItem(StandardItemModelParents parent) {
        this(parent.getModelLocation());
    }

    public ModelItem(ModelLocation parent) {
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
    public ModelItem addTextureLayer(TextureLocation textureLocation) {
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
    public ModelItem addTextureLayers(TextureLocation... textureLocations) {
        layers.addAll(Arrays.asList(textureLocations));

        return this;
    }
}
