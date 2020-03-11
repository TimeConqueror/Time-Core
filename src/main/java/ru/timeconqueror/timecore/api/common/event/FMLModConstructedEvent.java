package ru.timeconqueror.timecore.api.common.event;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.ModLifecycleEvent;

/**
 * This is the first called event during mod lifecycle startup.
 * <p>
 * Called after instantiating {@link Mod}-annotated class (directly after creating mod object and registering Event Subscribers for given mod file).
 * Called before {@link RegistryEvent.NewRegistry} and {@link RegistryEvent.Register} events.
 * <p>
 * This is a parallel dispatch event.
 * <p>
 * Posted by {@link Mod.EventBusSubscriber.Bus#MOD} event bus.
 */
public class FMLModConstructedEvent extends ModLifecycleEvent {
    public FMLModConstructedEvent(ModContainer container) {
        super(container);
    }
}
