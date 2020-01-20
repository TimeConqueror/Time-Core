package ru.timeconqueror.timecore.api.client.resource;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.TimeCore;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TimeResourceHolder {
    private HashMap<ResourceLocation, TimeResource> resources = new HashMap<>();
    private HashSet<String> domains = new HashSet<>();

    public void addResource(ResourceLocation location, TimeResource resource) {
        resources.put(location, resource);
    }

    public void addItemModel(Item item, ItemModel model) {
        ResourceLocation registryName = item.getRegistryName();

        if (registryName == null) {
            TimeCore.LOGGER.error("Can't register model location for the item without a registry name.", new RuntimeException());
            return;
        }

        resources.put(new ResourceLocation(registryName.getNamespace(), "models/item/" + registryName.getPath() + ".json"), model);
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

    HashMap<ResourceLocation, TimeResource> getResources() {
        return resources;
    }
}
