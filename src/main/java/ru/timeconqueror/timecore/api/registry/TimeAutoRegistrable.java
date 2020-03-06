package ru.timeconqueror.timecore.api.registry;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.objectweb.asm.Type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If target class inherits {@link ForgeTimeRegistry}, it will be registered to event bus,
 * so all methods annotated with {@link net.minecraftforge.eventbus.api.SubscribeEvent} will work.
 * <p>
 * If target class inherits {@link Initable},
 * its method {@link Initable#onInit(FMLCommonSetupEvent)} will be called during {@link FMLCommonSetupEvent} event.
 * <p>
 * <b><font color="yellow">WARNING: Annotated registry class must contain constructor without params or exception will be thrown.</b>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TimeAutoRegistrable {
    Type ASM_TYPE = Type.getType(TimeAutoRegistrable.class);
    Class<?>[] compatibleClasses = new Class[]{ForgeTimeRegistry.class, Initable.class};
}
