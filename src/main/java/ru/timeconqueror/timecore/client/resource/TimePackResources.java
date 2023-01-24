package ru.timeconqueror.timecore.client.resource;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.api.client.resource.GlobalResourceStorage;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

@ParametersAreNonnullByDefault
public class TimePackResources implements PackResources {
    private final String packId;

    public TimePackResources(String packId) {
        this.packId = packId;
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getRootResource(String... pElements) {
        return null;
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
        if (type == PackType.CLIENT_RESOURCES) {
            return GlobalResourceStorage.INSTANCE.getResource(location);
        }

        return null;
    }

    @Override
    public void listResources(PackType type, String pNamespace, String pPath, ResourceOutput pResourceOutput) {
        if (type == PackType.CLIENT_RESOURCES) {
            GlobalResourceStorage.INSTANCE.listResources(pNamespace, pPath, pResourceOutput);
        }
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

    @Override
    @NotNull
    public String packId() {
        return packId;
    }

    @Override
    public void close() {

    }
}
