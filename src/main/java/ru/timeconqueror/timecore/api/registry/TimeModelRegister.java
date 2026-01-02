package ru.timeconqueror.timecore.api.registry;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import ru.timeconqueror.timecore.api.registry.base.TaskHolder;
import ru.timeconqueror.timecore.client.render.model.InFileLocation;
import ru.timeconqueror.timecore.internal.client.handlers.ClientLoadingHandler;

//TODO check on server side
//TODO javadoc
public class TimeModelRegister extends TimeRegister {
    private final TaskHolder<InFileLocation> locations = TaskHolder.make(FMLClientSetupEvent.class);

    public TimeModelRegister(String modId) {
        super(modId);
    }

    public InFileLocation register(String path) {
        return register(path, InFileLocation.WILDCARD);
    }

    public InFileLocation register(String path, String modelName) {
        InFileLocation tml = new InFileLocation(ResourceLocation.fromNamespaceAndPath(getModId(), path), modelName);
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
