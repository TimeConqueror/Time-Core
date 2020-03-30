package ru.timeconqueror.timecore.misc;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.moddiscovery.ModAnnotation;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.forgespi.language.ModFileScanData;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.registry.Initable;
import ru.timeconqueror.timecore.api.registry.TimeAutoRegistrable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ModInitializer {
    public static void setupAutoRegistries() {
        for (ModInfo modInfo : ModList.get().getMods()) {
            if (modInfo.getModId().equals(ModLoadingContext.get().getActiveNamespace())) {
                ModFileScanData scanData = modInfo.getOwningFile().getFile().getScanResult();

                scanData.getAnnotations().stream()
                        .filter(annotationData -> annotationData.getAnnotationType().equals(TimeAutoRegistrable.ASM_TYPE))
                        .forEach(annotationData -> {
                            try {
                                final Class<?> regClass = Class.forName(annotationData.getClassType().getClassName());

                                List<Field> instanceFieldList = Arrays.stream(regClass.getFields())
                                        .filter(f -> Modifier.isPublic(f.getModifiers()) && Modifier.isStatic(f.getModifiers()) && Modifier.isFinal(f.getModifiers()))
                                        .filter(f -> f.isAnnotationPresent(TimeAutoRegistrable.Instance.class))
                                        .collect(Collectors.toList());

                                if (instanceFieldList.size() > 1) {
                                    throw new RuntimeException("Found more then one field annotated with " + TimeAutoRegistrable.Instance.class + " in " + regClass + " of mod " + ModLoadingContext.get().getActiveNamespace() + ". Should be only one.");
                                }

                                Object instance = null;
                                if (!instanceFieldList.isEmpty()) {
                                    Field f = instanceFieldList.get(0);
                                    if (f.getType().equals(regClass)) {
                                        try {
                                            instance = f.get(null);
                                            if (instance == null)
                                                throw new RuntimeException("Provided field " + f.getName() + " of " + regClass + " with an instance contains null!");
                                        } catch (IllegalAccessException e) {
                                            throw new RuntimeException("Can't get an instance in " + f.getName() + " of " + regClass, e);
                                        }
                                    } else {
                                        throw new RuntimeException("Field " + f.getName() + " from " + regClass + " should have the same type as a class.");
                                    }
                                }

                                if (instance != null) {
                                    FMLJavaModLoadingContext.get().getModEventBus().register(instance);
                                    TimeCore.LOGGER.debug("{}: Registered Event Subscriber as instance in field {} of class: {}", ModLoadingContext.get().getActiveNamespace(), instanceFieldList.get(0).getName(), regClass);
                                    return;
                                }

                                //code will achieve this point if we didn't find instance in class fields.
                                final ModAnnotation.EnumHolder targetHolder = (ModAnnotation.EnumHolder) annotationData.getAnnotationData().getOrDefault("target", new ModAnnotation.EnumHolder(null, "INSTANCE"));
                                TimeAutoRegistrable.Target target = TimeAutoRegistrable.Target.valueOf(targetHolder.getValue());

                                try {
                                    instance = regClass.newInstance();
                                } catch (ReflectiveOperationException e) {
                                    if (e.getCause() instanceof NoSuchMethodException) {
                                        throw new RuntimeException("TimeCore AutoRegistry can't find constructor with no parameters for " + regClass, e);
                                    } else {
                                        throw new RuntimeException(e);
                                    }
                                }

                                if (target == TimeAutoRegistrable.Target.CLASS) {
                                    FMLJavaModLoadingContext.get().getModEventBus().register(regClass);
                                    TimeCore.LOGGER.debug("{}: Registered Event Subscriber as class: {}", ModLoadingContext.get().getActiveNamespace(), regClass);
                                } else {
                                    FMLJavaModLoadingContext.get().getModEventBus().register(instance);
                                    TimeCore.LOGGER.debug("{}: Registered Event Subscriber as instance: {}", ModLoadingContext.get().getActiveNamespace(), regClass);
                                }

                                if (Initable.class.isAssignableFrom(regClass)) {//TODO remove
                                    FMLJavaModLoadingContext.get().getModEventBus().addListener(((Initable) instance)::onInit);
                                }
                            } catch (ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        });
                break;
            }
        }
    }
}
