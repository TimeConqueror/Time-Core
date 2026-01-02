package ru.timeconqueror.timecore.api.registry;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import ru.timeconqueror.timecore.api.registry.base.TaskHolder;

public class AdvancementCriterionRegister extends TimeRegister {
    private final TaskHolder<Runnable> criteriaToRegister = TaskHolder.make(FMLCommonSetupEvent.class);

    public AdvancementCriterionRegister(String modid) {
        super(modid);
    }

    public <I extends CriterionTriggerInstance, T extends CriterionTrigger<I>> T register(String name, T criterion) {
        criteriaToRegister.add(() -> CriteriaTriggers.register(getModId() + ":" + name, criterion));
        return criterion;
    }

    @Override
    public void regToBus(IEventBus modEventBus) {
        super.regToBus(modEventBus);
        modEventBus.addListener(this::onSetup);
    }

    private void onSetup(FMLCommonSetupEvent event) {
        enqueueWork(event, () -> criteriaToRegister.doForEachAndRemove(Runnable::run));
    }
}
