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
 * Otherwise, it will be registered to the {@link EventBusSubscriber.Bus#MOD}
 * (events are fired on the mod event bus when they should be handled during initialization of a mod),
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

    /**
     * Name of the field in this class, which will contain created instance.
     * If it equals empty string, then system will consider, that there is no field to place instance in.
     * <br>
     * Only works when {@link #target()} equals {@link Target#INSTANCE}.
     * <br>
     * The field should be the same type as class.
     * Field should be static.
     * Field can have any access modifier.
     * <pre>
     *     {@literal @}TimeAutoRegisterable(target = INSTANCE, instance = "INSTANCE")
     *     public class TestClass {
     *         public final TestClass INSTANCE = null;
     *     }
     * </pre>
     */
    String instance() default "";

    enum Target {
        INSTANCE,
        CLASS
    }
}
