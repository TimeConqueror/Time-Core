package ru.timeconqueror.timecore.api.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import ru.timeconqueror.timecore.api.registry.base.TaskHolder;
import ru.timeconqueror.timecore.client.render.model.TimeModelLocation;
import ru.timeconqueror.timecore.internal.client.handlers.ClientLoadingHandler;

public class TimeModelRegister extends TimeRegister {
    private final TaskHolder<TimeModelLocation> locations = TaskHolder.make(FMLClientSetupEvent.class);

    public TimeModelRegister(String modId) {
        super(modId);
    }

    public TimeModelLocation register(String path) {
        return register(path, TimeModelLocation.WILDCARD);
    }

    public TimeModelLocation register(String path, String modelName) {
        TimeModelLocation tml = new TimeModelLocation(new ResourceLocation(getModId(), path), modelName);
        locations.add(tml);

        return tml;
    }

    @Override
    public void regToBus(IEventBus modEventBus) {
        super.regToBus(modEventBus);

        modEventBus.addListener(this::onClientSetup);
    }

    private void onClientSetup(FMLClientSetupEvent e) {
        catchErrors(e, () -> locations.doAndRemove(ClientLoadingHandler.MODEL_SET::regModelLocations));
    }
}