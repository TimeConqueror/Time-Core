package ru.timeconqueror.timecore.registry.newreg;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraftforge.eventbus.api.IEventBus;

public class AdvancementCriterionRegister extends TimeRegister {
    public AdvancementCriterionRegister(String modid) {
        super(modid);
    }

    public static <I extends ICriterionInstance, T extends ICriterionTrigger<I>> T register(T criterion) {
        return CriteriaTriggers.register(criterion);
    }

    @Override
    public void regToBus(IEventBus bus) {

    }
}
