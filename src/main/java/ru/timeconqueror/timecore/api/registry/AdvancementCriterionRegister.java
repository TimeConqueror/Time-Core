package ru.timeconqueror.timecore.api.registry;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import ru.timeconqueror.timecore.api.registry.base.TaskHolder;

public class AdvancementCriterionRegister extends TimeRegister {
    private final TaskHolder<CriterionTrigger<?>> criteria = TaskHolder.make(FMLCommonSetupEvent.class);

    public AdvancementCriterionRegister(String modid) {
        super(modid);
    }

    public <I extends CriterionTriggerInstance, T extends CriterionTrigger<I>> T register(T criterion) {
        criteria.add(criterion);
        return criterion;
    }

    @Override
    public void regToBus(IEventBus modEventBus) {
        super.regToBus(modEventBus);
        modEventBus.addListener(this::onSetup);
    }

    private void onSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> criteria.doForEachAndRemove(CriteriaTriggers::register));
    }
}
