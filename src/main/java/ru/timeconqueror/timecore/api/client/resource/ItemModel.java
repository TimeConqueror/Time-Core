package ru.timeconqueror.timecore.api.client.resource;

import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;

public class ItemModel extends TimeJSONResource {
    public static int GENERATED = 0;
    public static int HANDHELD = 1;

    /**
     * Represents the location of the model.
     * You should provide it in the constructor without <b>{@code 'models/'}</b> part and <b>file extension</b> in {@code path} param.<p>
     * Example: given resource location - {@code new ResourceLocation("mymod", "item/test_item");}<p>
     * Real location from given: {@code mymod:models/item/test_item.json}
     */
    private ResourceLocation parent;
    private ArrayList<ResourceLocation> layers = new ArrayList<>(1);

    public ItemModel(StandardItemModelParents parent) {
        this.parent = parent.getResourceLocation();
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

    public ItemModel addTextureLayer(ResourceLocation textureLocation) {
        layers.add(textureLocation);

        return this;
    }

    public ItemModel addTextureLayers(ResourceLocation... textureLocations) {
        layers.addAll(Arrays.asList(textureLocations));

        return this;
    }
}
