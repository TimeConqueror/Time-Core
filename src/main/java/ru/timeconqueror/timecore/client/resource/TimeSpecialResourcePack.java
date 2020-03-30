package ru.timeconqueror.timecore.client.resource;

import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.api.client.TimeClient;
import ru.timeconqueror.timecore.api.client.resource.TimeResource;
import ru.timeconqueror.timecore.api.client.resource.TimeResourceHolder;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TimeSpecialResourcePack implements IResourcePack {
    private final TimeResourceHolder resourceHolder = TimeClient.RESOURCE_HOLDER;

    @NotNull
    @Override
    public InputStream getRootResourceStream(@NotNull String fileName) {
        throw new UnsupportedOperationException("TimeCore ResourcePacks can't have root resources.");
    }

    @NotNull
    @Override
    public InputStream getResourceStream(@NotNull ResourcePackType type, @NotNull ResourceLocation location) throws IOException {
        if (type == ResourcePackType.CLIENT_RESOURCES) {
            TimeResource resource = resourceHolder.getResource(location);
            if (resource != null) {
                return resource.getInputStream();
            }

            throw new FileNotFoundException("Can't find " + location + " " + getName());
        } else {
            throw new UnsupportedOperationException("TimeCore ResourcePacks supports only client resources.");
        }
    }

    @NotNull
    @Override
    public Collection<ResourceLocation> getAllResourceLocations(@NotNull ResourcePackType type, @NotNull String pathIn, int maxDepth, @NotNull Predicate<String> filter) {
        if (type != ResourcePackType.CLIENT_RESOURCES) {
            return Collections.emptyList();
        }

        return resourceHolder.getResources().keySet().stream()
                .filter(location -> location.getPath().startsWith(pathIn) && filter.test(location.getPath()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean resourceExists(@NotNull ResourcePackType type, @NotNull ResourceLocation location) {
        if (type == ResourcePackType.SERVER_DATA) return false;

        return resourceHolder.hasResource(location);
    }

    @NotNull
    @Override
    public Set<String> getResourceNamespaces(@NotNull ResourcePackType type) {
        return type == ResourcePackType.CLIENT_RESOURCES ? resourceHolder.getDomains() : Collections.emptySet();
    }

    @Nullable
    @Override
    public <T> T getMetadata(@NotNull IMetadataSectionSerializer<T> deserializer) {
        return null;
    }

    @NotNull
    @Override
    public String getName() {
        return "TimeCore Special Resources";
    }

    @Override
    public void close() {

    }
}
