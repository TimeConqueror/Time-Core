package ru.timeconqueror.timecore.devtools.gen.advancement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.timeconqueror.timecore.mod.mixins.accessor.UnlockedAdvancementProvider;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TimeAdvancementGenerator implements IDataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final DataGenerator generator;
    private final List<AdvancementSet> setList = new ArrayList<>();

    public TimeAdvancementGenerator(DataGenerator generatorIn) {
        this.generator = generatorIn;
    }

    public void run(DirectoryCache cache) {
        Path outputFolder = this.generator.getOutputFolder();

        Set<ResourceLocation> set = new HashSet<>();
        ISaveFunction saveFunction = (advancementBuilder, id) -> {
            Advancement advancement = advancementBuilder.build(id);

            if (!set.add(advancement.getId())) {
                throw new IllegalStateException("Duplicate advancement " + advancement.getId());
            } else {
                Path savePath = UnlockedAdvancementProvider.createPath(outputFolder, advancement);

                try {
                    IDataProvider.save(GSON, cache, advancement.deconstruct().serializeToJson(), savePath);
                } catch (IOException ioexception) {
                    LOGGER.error("Couldn't save advancement {}", savePath, ioexception);
                }

            }

            return advancement;
        };

        for (AdvancementSet advancementSet : this.setList) {
            advancementSet.fill(saveFunction);
        }
    }

    public TimeAdvancementGenerator addSet(AdvancementSet advancementSet) {
        setList.add(advancementSet);

        return this;
    }

    @Override
    public String getName() {
        return "TimeCore Advancements";
    }
}
