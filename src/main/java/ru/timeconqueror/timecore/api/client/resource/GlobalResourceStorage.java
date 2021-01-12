package ru.timeconqueror.timecore.api.client.resource;

import net.minecraft.util.ResourceLocation;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.Markers;
import ru.timeconqueror.timecore.storage.LoadingOnlyStorage;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public enum GlobalResourceStorage {
    INSTANCE;

    private final HashMap<ResourceLocation, byte[]> resources = new HashMap<>();
    private final HashSet<String> domains = new HashSet<>();

    public void setup(String modId) {
        if (domains.add(modId)) {
            TimeCore.LOGGER.debug(Markers.RESOURCE_SYSTEM, "Domain {} was added to the resource holder.", modId);
        }
    }

    public void fill(Iterable<TimeResourceHolder> holders) {
        holders.forEach(holder -> holder.getResources().forEach((location, resource) -> {
            if (resources.put(location, resource.toBytes()) == null) {
                TimeCore.LOGGER.debug(Markers.RESOURCE_SYSTEM, "Added new resource with location: {}. Content: {}", location, resource.toString());
            } else {
                TimeCore.LOGGER.debug(Markers.RESOURCE_SYSTEM, "Overridden resource with location: {}. New content: {}", location, resource.toString());
            }
        }));
    }

    public InputStream getResource(ResourceLocation location) throws FileNotFoundException {
        byte[] resource = resources.get(location);
        if (resource != null) {
            return new ByteArrayInputStream(resource);
        }

        throw new FileNotFoundException("Can't find resource with " + location + " in TimeCore's GlobalResourceStorage");
    }

    public Collection<ResourceLocation> getResources(String namespaceIn, String pathIn, Predicate<String> filter) {
        LoadingOnlyStorage.tryLoadResourceHolders();

        return resources.keySet().stream()
                .filter(location -> location.getNamespace().equals(namespaceIn))
                .filter(location -> location.getPath().startsWith(pathIn) && filter.test(location.getPath()))
                .collect(Collectors.toList());
    }

    public boolean hasResource(ResourceLocation location) {
        return resources.containsKey(location);
    }

    public Set<String> getDomains() {
        return Collections.unmodifiableSet(domains);
    }
}
