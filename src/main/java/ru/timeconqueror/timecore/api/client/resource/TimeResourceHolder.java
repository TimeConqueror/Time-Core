package ru.timeconqueror.timecore.api.client.resource;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.client.resource.location.BlockModelLocation;
import ru.timeconqueror.timecore.util.ObjectUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TimeResourceHolder {
    private HashMap<ResourceLocation, TimeResource> resources = new HashMap<>();
    private HashSet<String> domains = new HashSet<>();

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
     * @param item  used to provide path to model.
     * @param model resource that will be available on created path.
     */
    public void addItemModel(Item item, ItemModel model) {
        ResourceLocation registryName = item.getRegistryName();

        if (!ObjectUtils.checkIfNotNull(registryName, "Can't register model location for the item without a registry name.")) {
            return;
        }

        resources.put(ResourceHelper.toItemModelLocation(registryName), model);
    }

    /**
     * Adds item model that will be available with path:
     * blockNameSpace:blockstates/blockName.json
     *
     * @param block              used to provide path to model.
     * @param blockStateResource resource that will be available on created path.
     */
    public void addBlockStateResource(Block block, BlockStateResource blockStateResource) {
        ResourceLocation registryName = block.getRegistryName();

        if (!ObjectUtils.checkIfNotNull(registryName, "Can't register blockstate location for the block without a registry name.")) {
            return;
        }

        resources.put(ResourceHelper.toBlockStateLocation(registryName), blockStateResource);
    }

    /**
     * Adds item model that will be available with path:
     * blockNameSpace:models/block/.json
     *
     * @param block used to provide path to model.
     * @param model resource that will be available on created path.
     */
    public void addBlockModel(Block block, BlockModel model) {
        ResourceLocation registryName = block.getRegistryName();

        if (!ObjectUtils.checkIfNotNull(registryName, "Can't register model location for the block without a registry name.")) {
            return;
        }

        addBlockModel(new BlockModelLocation(registryName.getNamespace(), registryName.getPath()), model);
    }

    public void addBlockModel(BlockModelLocation location, BlockModel model) {
        resources.put(location.fullLocation(), model);
    }

    @Nullable
    public TimeResource getResource(ResourceLocation location) {
        return resources.get(location);
    }

    public boolean hasResource(ResourceLocation location) {
        return resources.containsKey(location);
    }

    public void addDomain(String domain) {
        domains.add(domain);
    }

    public boolean hasDomain(String domain) {
        return domains.contains(domain);
    }

    public Set<String> getDomains() {
        return Collections.unmodifiableSet(domains);
    }

    public HashMap<ResourceLocation, TimeResource> getResources() {
        return resources;
    }
}
