package ru.timeconqueror.timecore.api.registry;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import org.objectweb.asm.Type;
import ru.timeconqueror.timecore.api.registry.base.TaskHolder;
import ru.timeconqueror.timecore.mixins.accessor.CapabilityManagerAccessor;

//ToDo javadoc
public class CapabilityRegister extends TimeRegister {
    private final TaskHolder<Class<?>> capClasses = TaskHolder.make(RegisterCapabilitiesEvent.class);

    public CapabilityRegister(String modId) {
        super(modId);
    }

    public <T> Capability<T> register(Class<T> type) {
        capClasses.get().add(type);

        String internalName = Type.getInternalName(type);
        return ((CapabilityManagerAccessor) (Object) CapabilityManager.INSTANCE).callGet(internalName, false);
    }

    @Override
    public void regToBus(IEventBus modEventBus) {
        super.regToBus(modEventBus);
        modEventBus.addListener(this::registerCaps);
    }

    private void registerCaps(RegisterCapabilitiesEvent event) {
        capClasses.doForEachAndRemove(event::register);
    }
}
