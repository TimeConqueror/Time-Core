package ru.timeconqueror.timecore.api.client.resource;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.client.resource.location.BlockModelLocation;
import ru.timeconqueror.timecore.api.util.ObjectUtils;

import java.util.HashMap;

public class TimeResourceHolder {
    private final HashMap<ResourceLocation, TimeResource> resources = new HashMap<>();

    /**
     * Adds {@code resource} to the internal resourcepack. It will be available on given location.
     *
     * @param location location to put given resource.
     */
    public void addResource(ResourceLocation location, TimeResource resource) {
        resources.put(location, resource);
    }

    /**
     * Adds item model that will be available with path:
     * itemNameSpace:models/item/itemName.json
     *
     * @param registryName registry name of item which is used to provide path to model.
     * @param model        resource that will be available on created path.
     */
    public void addItemModel(ResourceLocation registryName, ItemModel model) {
        if (!ObjectUtils.checkNotNull(registryName, "Can't register model location for the item without a registry location.")) {
            return;
        }

        addResource(LocationResolver.toItemModelLocation(registryName), model);
    }

    /**
     * Adds item model that will be available with path:
     * blockNameSpace:blockstates/blockName.json
     *
     * @param registryName       registry name of block which is used to provide path to model.
     * @param blockStateResource resource that will be available on created path.
     */
    public void addBlockStateResource(ResourceLocation registryName, BlockStateResource blockStateResource) {
        if (!ObjectUtils.checkNotNull(registryName, "Can't register blockstate location for the block without a registry location.")) {
            return;
        }

        addResource(LocationResolver.toBlockStateLocation(registryName), blockStateResource);
    }

    /**
     * Adds item model that will be available with path:
     * blockNameSpace:models/block/.json
     *
     * @param registryName registry name of block which is used to provide path to model.
     * @param model        resource that will be available on created path.
     */
    public void addBlockModel(ResourceLocation registryName, BlockModel model) {
        if (!ObjectUtils.checkNotNull(registryName, "Can't register model location for the block without a registry location.")) {
            return;
        }

        addBlockModel(new BlockModelLocation(registryName.getNamespace(), registryName.getPath()), model);
    }

    public void addBlockModel(BlockModelLocation location, BlockModel model) {
        addResource(location.fullLocation(), model);
    }

    @Nullable
    public TimeResource getResource(ResourceLocation location) {
        return resources.get(location);
    }

    public boolean hasResource(ResourceLocation location) {
        return resources.containsKey(location);
    }

    public HashMap<ResourceLocation, TimeResource> getResources() {
        return resources;
    }
}
