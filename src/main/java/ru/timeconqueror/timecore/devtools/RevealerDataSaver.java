package ru.timeconqueror.timecore.devtools;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.structure.Structure;
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

    void saveOnServer(Multimap<UUID, Structure<?>> subscribedStructures) {
        File saveFile = getServerPathToFile();
        saveNBTToFile(serializeStructureInfo(subscribedStructures), saveFile);
    }

    void saveOnClient(ClientSettings clientSettings) {
        File saveFile = geClientPathToFile();
        saveNBTToFile(serializeClientSettings(clientSettings), saveFile);
    }

    ClientSettings restoreOnClient() {
        File saveFile = geClientPathToFile();

        CompoundNBT compoundNBT = restoreNBT(saveFile);

        if (compoundNBT == null) {
            return new ClientSettings(new HashMap<>(), false);
        } else {
            return deserializeClientSettings(compoundNBT);
        }
    }

    Multimap<UUID, Structure<?>> restoreOnServer() {
        File saveFile = getServerPathToFile();

        CompoundNBT nbt = restoreNBT(saveFile);

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

    private CompoundNBT serializeClientSettings(ClientSettings clientSettings) {
        Map<ResourceLocation, Integer> structureColorMap = clientSettings.structureColorMap;
        boolean visibleThroughBlocks = clientSettings.visibleThroughBlocks;

        CompoundNBT out = new CompoundNBT();

        CompoundNBT structureMapNBT = new CompoundNBT();
        structureColorMap.forEach((structureName, color) -> {
            structureMapNBT.putInt(structureName.toString(), color);
        });

        out.put("structure_colors", structureMapNBT);
        out.putBoolean("visible_through_blocks", visibleThroughBlocks);

        return out;
    }

    private ClientSettings deserializeClientSettings(CompoundNBT in) {
        CompoundNBT structureMapNBT = in.getCompound("structure_colors");
        Map<ResourceLocation, Integer> structureColorMap = new HashMap<>(structureMapNBT.size());
        structureMapNBT.getAllKeys().forEach(key -> structureColorMap.put(new ResourceLocation(key), structureMapNBT.getInt(key)));

        boolean visibleThroughBlocks = in.getBoolean("visible_through_blocks");

        return new ClientSettings(structureColorMap, visibleThroughBlocks);
    }

    private CompoundNBT serializeStructureInfo(Multimap<UUID, Structure<?>> subscribedStructures) {
        CompoundNBT out = new CompoundNBT();

        subscribedStructures.keys().forEach(uuid -> {
            Collection<Structure<?>> structures = subscribedStructures.get(uuid);

            ListNBT structuresNBT = new ListNBT();

            if (structures != null) {
                structures.forEach(structure -> {
                    ResourceLocation registryName = structure.getRegistryName();
                    structuresNBT.add(StringNBT.valueOf(registryName.toString()));
                });
            }

            out.put(uuid.toString(), structuresNBT);
        });

        return out;
    }

    private Multimap<UUID, Structure<?>> deserializeStructureInfo(CompoundNBT in) {
        Multimap<UUID, Structure<?>> out = ArrayListMultimap.create();

        for (String uuidString : in.getAllKeys()) {
            ListNBT structures = (ListNBT) in.get(uuidString);

            UUID uuid = UUID.fromString(uuidString);

            for (int i = 0, structuresSize = structures.size(); i < structuresSize; i++) {
                String name = structures.getString(i);
                ResourceLocation rl = new ResourceLocation(name);
                out.put(uuid, ForgeRegistries.STRUCTURE_FEATURES.getValue(rl));
            }
        }

        return out;
    }

    private void saveNBTToFile(CompoundNBT compoundNBT, File saveFile) {
        ObjectUtils.runWithCatching(IOException.class, () -> {
            FileUtils.prepareFileForWrite(saveFile);
            CompressedStreamTools.write(compoundNBT, saveFile);
        });
    }

    @Nullable
    private CompoundNBT restoreNBT(File saveFile) {
        return ObjectUtils.getWithCatching(IOException.class, () -> CompressedStreamTools.read(saveFile));
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
