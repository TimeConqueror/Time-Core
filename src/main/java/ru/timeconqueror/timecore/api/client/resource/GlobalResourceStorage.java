package ru.timeconqueror.timecore.api.client.resource;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.Nullable;
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

    @Nullable
    public IoSupplier<InputStream> getResource(ResourceLocation location) {
        byte[] resource = resources.get(location);
        if (resource != null) {
            return () -> new ByteArrayInputStream(resource);
        }

       return null;
    }

    public void listResources(String namespaceIn, String pathIn, PackResources.ResourceOutput resourceOutput) {
        LoadingOnlyStorage.tryLoadResourceHolders();
        resources.keySet().stream()
                .filter(location -> location.getNamespace().equals(namespaceIn))
                .filter(location -> location.getPath().startsWith(pathIn))
                .forEach(location -> resourceOutput.accept(location, getResource(location)));
    }

    public Set<String> getDomains() {
        return Collections.unmodifiableSet(domains);
    }
}
