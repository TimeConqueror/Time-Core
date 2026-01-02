package ru.timeconqueror.timecore.structureio;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.util.BlockPosUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

//FIXME add error printing upon file not found or any other io exception on load template
@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StructureIO {
    public static final StructureIO INSTANCE = new StructureIO();

    private final Map<String, ExtendedStructureTemplate> cachedTemplates = new HashMap<>();

    public void save(ServerLevel level, BlockPos pos1, BlockPos pos2, Path path, boolean includeEntities, @Nullable Block ignoredBlock, BlockPos sourcePosition) {
        StructureTemplate template = new StructureTemplate();

        var start = BlockPosUtils.makeMin(pos1, pos2);
        var end = BlockPosUtils.makeMax(pos1, pos2);
        var size = end.subtract(start).offset(1, 1, 1);
        template.fillFromWorld(level, start, size, includeEntities, ignoredBlock);

        CompoundTag structureTag = new CompoundTag();
        template.save(structureTag);

        BlockPos genOffset = sourcePosition.subtract(start);
        structureTag.putLong("lg_offset", genOffset.asLong());

        saveStructureTagToFile(path, structureTag);
    }

    public ExtendedStructureTemplate getOrLoadTemplateFromFile(File file) {
        String pathStr = file.getAbsoluteFile().toString();

        ExtendedStructureTemplate template = cachedTemplates.get(pathStr);

        if (template == null) {
            template = loadTemplate(file)
                    .map(loadedTemplate -> {
                        cachedTemplates.put(pathStr, loadedTemplate);
                        return loadedTemplate;
                    })
                    .orElse(ExtendedStructureTemplate.DUMMY);
        }

        return template;
    }

    public Optional<ExtendedStructureTemplate> loadTemplate(File file) {
        try (var is = new FileInputStream(file)) {
            return loadTemplate(is);
        } catch (IOException ex) {
            log.error("Failed to read structure tag from stream", ex);
            return Optional.empty();
        }
    }

    public Optional<ExtendedStructureTemplate> loadTemplate(InputStream stream) throws IOException {
        return loadStructureTagFromStream(stream)
                .map(compoundTag -> {
                    StructureTemplate template = new StructureTemplate();
                    //noinspection deprecation
                    template.load(BuiltInRegistries.BLOCK.asLookup(), compoundTag);
                    BlockPos genOffset = BlockPos.of(compoundTag.getLong("lg_offset"));
                    return new ExtendedStructureTemplate(template, genOffset);
                });
    }

    public void generate(ExtendedStructureTemplate template, ServerLevel level, BlockPos start, StructurePlaceSettings structurePlaceSettings) {
        start = start.subtract(template.getGenOffset());
        generate(template.getTemplate(), level, start, structurePlaceSettings);
    }

    public void generate(StructureTemplate template, ServerLevel level, BlockPos start, StructurePlaceSettings structurePlaceSettings) {
        template.placeInWorld(level, start, start, structurePlaceSettings, RandomSource.create(), Block.UPDATE_CLIENTS);
    }

    private void saveStructureTagToFile(Path structurePath, CompoundTag structureTag) {
        Path structureDir = structurePath.getParent();
        if (structureDir != null) {
            try {
                Files.createDirectories(Files.exists(structureDir) ? structureDir.toRealPath() : structureDir);
            } catch (IOException ioexception) {
                log.error("Failed to create parent directory: {}", structureDir);
                return;
            }
        }

        try {
            NbtIo.writeCompressed(structureTag, structurePath);
        } catch (IOException e) {
            log.error("Failed to write structure tag to file: {}", structurePath, e);
        }
    }

    private Optional<CompoundTag> loadStructureTagFromStream(InputStream stream) throws IOException {
        return Optional.of(NbtIo.readCompressed(stream, NbtAccounter.unlimitedHeap()));
    }
}
