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
import ru.timeconqueror.timecore.util.EnvironmentUtils;
import ru.timeconqueror.timecore.util.FileUtils;
import ru.timeconqueror.timecore.util.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.UUID;

public class RevealerDataSaver {
    private static final String SAVE_PATH = "TimeCore/debug/structures.nbt";

    void save(Multimap<UUID, Structure<?>> subscribedStructures) {
        File saveFile = getPathToFile();

        ObjectUtils.runWithCatching(IOException.class, () -> {
            FileUtils.prepareFileForWrite(saveFile);

            CompoundNBT serialized = serialize(subscribedStructures);

            CompressedStreamTools.write(serialized, saveFile);
        });
    }

    Multimap<UUID, Structure<?>> restore() {
        File saveFile = getPathToFile();

        return ObjectUtils.getWithCatching(IOException.class, () -> {
            CompoundNBT nbt = CompressedStreamTools.read(saveFile);

            if (nbt == null) {
                return ArrayListMultimap.create();
            } else {
                return deserialize(nbt);
            }
        });
    }

    private File getPathToFile() {
        Path worldSaveDir = EnvironmentUtils.getWorldSaveDir();
        return worldSaveDir.resolve(SAVE_PATH).toFile();
    }

    private CompoundNBT serialize(Multimap<UUID, Structure<?>> subscribedStructures) {
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

    private Multimap<UUID, Structure<?>> deserialize(CompoundNBT in) {
        Multimap<UUID, Structure<?>> out = ArrayListMultimap.create();

        for (String uuidString : in.keySet()) {
            ListNBT structures = (ListNBT) in.get(uuidString);

            UUID uuid = UUID.fromString(uuidString);

            for (int i = 0, structuresSize = structures.size(); i < structuresSize; i++) {
                String name = structures.getString(i);
                ResourceLocation rl = new ResourceLocation(name);
                out.put(uuid, ((Structure<?>) ForgeRegistries.FEATURES.getValue(rl)));
            }
        }

        return out;
    }
}
