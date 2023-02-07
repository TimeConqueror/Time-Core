package ru.timeconqueror.timecore.api.devtools.gen.advancement;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.List;

public class DataGeneration {
    /**
     * Makes simple to use advancement provider factory
     *
     * @see AdvancementProviderExtKt
     */
    public static DataProvider.Factory<ForgeAdvancementProvider> advancementProvider(GatherDataEvent event, ForgeAdvancementProvider.AdvancementGenerator... generators) {
        return output_ -> new ForgeAdvancementProvider(output_, event.getLookupProvider(), event.getExistingFileHelper(), List.of(generators));
    }

    /**
     * Method to clarify parameter for {@link DataGenerator#addProvider}
     */
    public static <T extends DataProvider> DataProvider.Factory<T> factory(DataProvider.Factory<T> factory) {
        return factory;
    }
}
