package ru.timeconqueror.timecore.client.resource;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.api.client.resource.GlobalResourceStorage;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

public class TimeSpecialResourcePack implements PackResources {
    @NotNull
    @Override
    public InputStream getRootResource(@NotNull String fileName) {
        throw new UnsupportedOperationException("TimeCore ResourcePacks can't have root resources.");
    }

    @NotNull
    @Override
    public InputStream getResource(@NotNull PackType type, @NotNull ResourceLocation location) throws IOException {
        if (type == PackType.CLIENT_RESOURCES) {
            return GlobalResourceStorage.INSTANCE.getResource(location);
        } else {
            throw new UnsupportedOperationException("TimeCore ResourcePacks supports only client resources.");
        }
    }

    @Override
    public Collection<ResourceLocation> getResources(PackType type, String namespaceIn, String pathIn, int maxDepthIn, Predicate<String> filter) {
        if (type == PackType.CLIENT_RESOURCES) {
            return GlobalResourceStorage.INSTANCE.getResources(namespaceIn, pathIn, filter);
        }

        return Collections.emptyList();
    }

    @Override
    public boolean hasResource(@NotNull PackType type, @NotNull ResourceLocation location) {
        return type == PackType.CLIENT_RESOURCES && GlobalResourceStorage.INSTANCE.hasResource(location);
    }

    @NotNull
    @Override
    public Set<String> getNamespaces(@NotNull PackType type) {
        return type == PackType.CLIENT_RESOURCES ? GlobalResourceStorage.INSTANCE.getDomains() : Collections.emptySet();
    }

    @Nullable
    @Override
    public <T> T getMetadataSection(@NotNull MetadataSectionSerializer<T> deserializer) {
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
