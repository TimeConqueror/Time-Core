package ru.timeconqueror.timecore.api.devtools.gen.advancement;

import net.minecraft.advancements.Advancement;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TimeAdvancementGenerator extends AdvancementProvider {
    private final List<IAdvancementSet> setList = new ArrayList<>();

    public TimeAdvancementGenerator(DataGenerator generatorIn, ExistingFileHelper fileHelperIn) {
        super(generatorIn, fileHelperIn);
    }

    @Override
    protected void registerAdvancements(Consumer<Advancement> consumer, ExistingFileHelper fileHelper) {
        super.registerAdvancements(consumer, fileHelper);
        for (IAdvancementSet set : this.setList) {
            set.fill((savePath, builder) -> {
                Advancement advancement = builder.build(savePath);
                consumer.accept(advancement);
                return advancement;
            });
        }
    }

    public TimeAdvancementGenerator addSet(IAdvancementSet advancementSet) {
        setList.add(advancementSet);

        return this;
    }

    @Override
    public String getName() {
        return "TimeCore Advancement Generator";
    }
}