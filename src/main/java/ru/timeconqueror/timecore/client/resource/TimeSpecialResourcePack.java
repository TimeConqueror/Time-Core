package ru.timeconqueror.timecore.client.resource;

import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.api.client.resource.GlobalResourceStorage;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

public class TimeSpecialResourcePack implements IResourcePack {
    @NotNull
    @Override
    public InputStream getRootResource(@NotNull String fileName) {
        throw new UnsupportedOperationException("TimeCore ResourcePacks can't have root resources.");
    }

    @NotNull
    @Override
    public InputStream getResource(@NotNull ResourcePackType type, @NotNull ResourceLocation location) throws IOException {
        if (type == ResourcePackType.CLIENT_RESOURCES) {
            return GlobalResourceStorage.INSTANCE.getResource(location);
        } else {
            throw new UnsupportedOperationException("TimeCore ResourcePacks supports only client resources.");
        }
    }

    @Override
    public Collection<ResourceLocation> getResources(ResourcePackType type, String namespaceIn, String pathIn, int maxDepthIn, Predicate<String> filter) {
        if (type == ResourcePackType.CLIENT_RESOURCES) {
            return GlobalResourceStorage.INSTANCE.getResources(namespaceIn, pathIn, filter);
        }

        return Collections.emptyList();
    }

    @Override
    public boolean hasResource(@NotNull ResourcePackType type, @NotNull ResourceLocation location) {
        return type == ResourcePackType.CLIENT_RESOURCES && GlobalResourceStorage.INSTANCE.hasResource(location);
    }

    @NotNull
    @Override
    public Set<String> getNamespaces(@NotNull ResourcePackType type) {
        return type == ResourcePackType.CLIENT_RESOURCES ? GlobalResourceStorage.INSTANCE.getDomains() : Collections.emptySet();
    }

    @Nullable
    @Override
    public <T> T getMetadataSection(@NotNull IMetadataSectionSerializer<T> deserializer) {
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
