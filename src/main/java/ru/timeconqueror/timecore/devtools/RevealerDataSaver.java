package ru.timeconqueror.timecore.devtools;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.util.EnvironmentUtils;
import ru.timeconqueror.timecore.api.util.FileUtils;
import ru.timeconqueror.timecore.api.util.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RevealerDataSaver {
    private static final String SERVER_SAVE_FILE = "TimeCore/debug/structure_revealer.nbt";
    private static final String CLIENT_SAVE_FILE = "TimeCore/debug/structure_revealer.nbt";

    void saveOnServer(Multimap<UUID, StructureFeature<?>> subscribedStructures) {
        File saveFile = getServerPathToFile();
        saveNBTToFile(serializeStructureInfo(subscribedStructures), saveFile);
    }

    void saveOnClient(ClientSettings clientSettings) {
        File saveFile = geClientPathToFile();
        saveNBTToFile(serializeClientSettings(clientSettings), saveFile);
    }

    ClientSettings restoreOnClient() {
        File saveFile = geClientPathToFile();

        CompoundTag compoundNBT = restoreNBT(saveFile);

        if (compoundNBT == null) {
            return new ClientSettings(new HashMap<>(), false);
        } else {
            return deserializeClientSettings(compoundNBT);
        }
    }

    Multimap<UUID, StructureFeature<?>> restoreOnServer() {
        File saveFile = getServerPathToFile();

        CompoundTag nbt = restoreNBT(saveFile);

        if (nbt == null) {
            return ArrayListMultimap.create();
        } else {
            return deserializeStructureInfo(nbt);
        }
    }

    private File getServerPathToFile() {
        return EnvironmentUtils.getWorldSaveDir().resolve(SERVER_SAVE_FILE).toFile();
    }

    private File geClientPathToFile() {
        return EnvironmentUtils.getConfigDir().resolve(CLIENT_SAVE_FILE).toFile();
    }

    private CompoundTag serializeClientSettings(ClientSettings clientSettings) {
        Map<ResourceLocation, Integer> structureColorMap = clientSettings.structureColorMap;
        boolean visibleThroughBlocks = clientSettings.visibleThroughBlocks;

        CompoundTag out = new CompoundTag();

        CompoundTag structureMapNBT = new CompoundTag();
        structureColorMap.forEach((structureName, color) -> {
            structureMapNBT.putInt(structureName.toString(), color);
        });

        out.put("structure_colors", structureMapNBT);
        out.putBoolean("visible_through_blocks", visibleThroughBlocks);

        return out;
    }

    private ClientSettings deserializeClientSettings(CompoundTag in) {
        CompoundTag structureMapNBT = in.getCompound("structure_colors");
        Map<ResourceLocation, Integer> structureColorMap = new HashMap<>(structureMapNBT.size());
        structureMapNBT.getAllKeys().forEach(key -> structureColorMap.put(new ResourceLocation(key), structureMapNBT.getInt(key)));

        boolean visibleThroughBlocks = in.getBoolean("visible_through_blocks");

        return new ClientSettings(structureColorMap, visibleThroughBlocks);
    }

    private CompoundTag serializeStructureInfo(Multimap<UUID, StructureFeature<?>> subscribedStructures) {
        CompoundTag out = new CompoundTag();

        subscribedStructures.keys().forEach(uuid -> {
            Collection<StructureFeature<?>> structures = subscribedStructures.get(uuid);

            ListTag structuresNBT = new ListTag();

            if (structures != null) {
                structures.forEach(structure -> {
                    ResourceLocation registryName = structure.getRegistryName();
                    structuresNBT.add(StringTag.valueOf(registryName.toString()));
                });
            }

            out.put(uuid.toString(), structuresNBT);
        });

        return out;
    }

    private Multimap<UUID, StructureFeature<?>> deserializeStructureInfo(CompoundTag in) {
        Multimap<UUID, StructureFeature<?>> out = ArrayListMultimap.create();

        for (String uuidString : in.getAllKeys()) {
            ListTag structures = (ListTag) in.get(uuidString);

            UUID uuid = UUID.fromString(uuidString);

            for (int i = 0, structuresSize = structures.size(); i < structuresSize; i++) {
                String name = structures.getString(i);
                ResourceLocation rl = new ResourceLocation(name);
                out.put(uuid, ForgeRegistries.STRUCTURE_FEATURES.getValue(rl));
            }
        }

        return out;
    }

    private void saveNBTToFile(CompoundTag compoundNBT, File saveFile) {
        ObjectUtils.runWithCatching(IOException.class, () -> {
            FileUtils.prepareFileForWrite(saveFile);
            NbtIo.write(compoundNBT, saveFile);
        });
    }

    @Nullable
    private CompoundTag restoreNBT(File saveFile) {
        return ObjectUtils.getWithCatching(IOException.class, () -> NbtIo.read(saveFile));
    }

    public static class ClientSettings {
        private final Map<ResourceLocation, Integer> structureColorMap;
        private final boolean visibleThroughBlocks;

        public ClientSettings(Map<ResourceLocation, Integer> structureColorMap, boolean visibleThroughBlocks) {
            this.structureColorMap = structureColorMap;
            this.visibleThroughBlocks = visibleThroughBlocks;
        }

        public Map<ResourceLocation, Integer> getStructureColorMap() {
            return structureColorMap;
        }

        public boolean isVisibleThroughBlocks() {
            return visibleThroughBlocks;
        }
    }
}
