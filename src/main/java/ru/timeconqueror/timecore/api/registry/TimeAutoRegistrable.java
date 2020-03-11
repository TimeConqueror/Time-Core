package ru.timeconqueror.timecore.api.registry;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.objectweb.asm.Type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If target class inherits {@link Initable},
 * its method {@link Initable#onInit(FMLCommonSetupEvent)} will be called during {@link FMLCommonSetupEvent} event.<br>
 * <p>
 * Otherwise, it will be registered to the {@link EventBusSubscriber.Bus#MOD},
 * so all non-static methods annotated with {@link SubscribeEvent} will work.<br>
 *
 * <b><font color="yellow">
 * WARNING: Annotated class with {@code target==Target.INSTANCE} must contain nullary constructor or exception will be thrown.
 * </b>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TimeAutoRegistrable {
    Type ASM_TYPE = Type.getType(TimeAutoRegistrable.class);

    /**
     * Depending on what is set, different listener creation behaviour is performed.
     *
     * <dl>
     *     <dt>INSTANCE</dt>
     *     <dd>Scanned for <em>non-static</em> methods annotated with {@link SubscribeEvent} and creates listeners for
     *     each method found.
     *     Requires a nullary constructor, or exception will be thrown.</dd>
     *     <dt>CLASS</dt>
     *     <dd>Scanned for <em>static</em> methods annotated with {@link SubscribeEvent} and creates listeners for
     *     each method found.</dd>
     * </dl>
     */
    Target target() default Target.INSTANCE;

    enum Target {
        INSTANCE,
        CLASS
    }
}
