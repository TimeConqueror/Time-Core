//package ru.timeconqueror.timecore.api.devtools.gen.advancement;
//// FIXME port
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import net.minecraft.advancements.Advancement;
//import net.minecraft.core.HolderLookup;
//import net.minecraft.data.*;
//import net.minecraft.data.advancements.AdvancementProvider;
//import net.minecraft.data.advancements.AdvancementSubProvider;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraftforge.common.data.ExistingFileHelper;
//import net.minecraftforge.common.data.ForgeAdvancementProvider;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import ru.timeconqueror.timecore.mixins.accessor.UnlockedAdvancementProvider;
//
//import java.io.IOException;
//import java.nio.file.Path;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.concurrent.CompletableFuture;
//import java.util.function.Consumer;
//
//public class TimeAdvancementProvider extends AdvancementProvider {
//    private static final Logger LOGGER = LogManager.getLogger();
//    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
//    private final List<IAdvancementSet> setList = new ArrayList<>();
//
//    /**
//     * Constructs an advancement provider using the generators to write the
//     * advancements to a file.
//     *
//     * @param output the target directory of the data generator
//     * @param registries a future of a lookup for registries and their objects
//     * @param existingFileHelper a helper used to find whether a file exists
//     * @param subProviders the generators used to create the advancements
//     */
//    public TimeAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper, List<ForgeAdvancementProvider.AdvancementGenerator> subProviders)
//    {
//        super(output, registries, subProviders.stream().map(generator -> generator.toSubProvider(existingFileHelper)).toList());
//    }
//
//    public void run(HashCache cache) {
//        Path outputFolder = this.generator.getOutputFolder();
//
//        Set<ResourceLocation> set = new HashSet<>();
//        ISaveFunction saveFunction = (id, advancementBuilder) -> {
//            Advancement advancement = advancementBuilder.build(id);
//
//            if (!set.add(advancement.getId())) {
//                throw new IllegalStateException("Duplicate advancement " + advancement.getId());
//            } else {
//                Path savePath = UnlockedAdvancementProvider.createPath(outputFolder, advancement);
//
//                try {
//                    DataProvider.save(GSON, cache, advancement.deconstruct().serializeToJson(), savePath);
//                } catch (IOException ioexception) {
//                    LOGGER.error("Couldn't save advancement {}", savePath, ioexception);
//                }
//
//            }
//
//            return advancement;
//        };
//
//        for (IAdvancementSet advancementSet : this.setList) {
//            advancementSet.fill(saveFunction);
//        }
//    }
//
//    public TimeAdvancementProvider addSet(IAdvancementSet advancementSet) {
//        setList.add(advancementSet);
//
//        return this;
//    }
//}
