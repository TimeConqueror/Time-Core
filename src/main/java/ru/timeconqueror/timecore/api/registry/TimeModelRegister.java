package ru.timeconqueror.timecore.api.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.api.util.Temporal;
import ru.timeconqueror.timecore.client.render.model.TimeModelLocation;
import ru.timeconqueror.timecore.internal.client.handlers.ClientLoadingHandler;

import java.util.ArrayList;
import java.util.List;

//TODO check on server side
public class TimeModelRegister extends TimeRegister {
    private final Temporal<List<TimeModelLocation>> locations = Temporal.of(new ArrayList<>(), "Called too late. Time models have already been registered.");

    public TimeModelRegister(String modId) {
        super(modId);
    }

    public TimeModelLocation register(String path) {
        return register(path, TimeModelLocation.SINGLE_MODEL_MASK);
    }

    public TimeModelLocation register(String path, @NotNull String modelName) {
        TimeModelLocation tml = new TimeModelLocation(new ResourceLocation(getModId(), path), modelName);
        locations.get().add(tml);

        return tml;
    }

    @Override
    public void regToBus(IEventBus modEventBus) {
        super.regToBus(modEventBus);

        modEventBus.addListener(this::onClientSetup);
    }

    private void onClientSetup(FMLClientSetupEvent e) {
        locations.doAndRemove(ClientLoadingHandler.MODEL_SET::regModelLocations);
    }
}
