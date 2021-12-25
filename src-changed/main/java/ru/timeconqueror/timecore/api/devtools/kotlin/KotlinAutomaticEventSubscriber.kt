package ru.timeconqueror.timecore.api.devtools.kotlin

import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.Logging
import net.minecraftforge.fml.ModContainer
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus
import net.minecraftforge.fml.loading.FMLEnvironment
import net.minecraftforge.fml.loading.moddiscovery.ModAnnotation.EnumHolder
import net.minecraftforge.forgespi.language.ModFileScanData
import net.minecraftforge.forgespi.language.ModFileScanData.AnnotationData
import org.apache.logging.log4j.LogManager
import org.objectweb.asm.Type
import java.lang.reflect.Modifier
import java.util.*
import java.util.stream.Collectors

/**
 * Automatic eventbus subscriber - reads [Mod.EventBusSubscriber]
 * annotations and passes the class instances to the [Mod.EventBusSubscriber.Bus]
 * defined by the annotation. Defaults to [MinecraftForge.EVENT_BUS]
 */
object KotlinAutomaticEventSubscriber {
    private val LOGGER = LogManager.getLogger()
    private val AUTO_SUBSCRIBER = Type.getType(EventBusSubscriber::class.java)
    private val MOD_TYPE = Type.getType(Mod::class.java)

    private val DEFAULT_SIDES = listOf(EnumHolder(null, "CLIENT"), EnumHolder(null, "DEDICATED_SERVER"))

    @JvmStatic
    fun inject(mod: ModContainer, scanData: ModFileScanData?, loader: ClassLoader?) {
        scanData ?: return

        LOGGER.debug(
            Logging.LOADING,
            "Attempting to inject @EventBusSubscriber Kotlin classes into the eventbus for {}",
            mod.modId
        )
        val ebsTargets = scanData.annotations.stream()
            .filter { AUTO_SUBSCRIBER == it.annotationType }
            .collect(Collectors.toList())

        val modids = scanData.annotations.stream()
            .filter { MOD_TYPE == it.annotationType }
            .collect(
                Collectors.toMap<AnnotationData, String, String>(
                    { it.classType.className },
                    { it.annotationData["value"] as String? })
            )

        ebsTargets.forEach { data ->
            @Suppress("UNCHECKED_CAST")
            val sidesValue = data.annotationData.getOrDefault("value", DEFAULT_SIDES) as List<EnumHolder>

            val sides = sidesValue.stream()
                .map { Dist.valueOf(it.value) }
                .collect(Collectors.toCollection { EnumSet.noneOf(Dist::class.java) })

            val modId = data.annotationData.getOrDefault(
                "modid", modids[data.classType.className]
                    ?: mod.modId
            ) as String

            val busTargetHolder = data.annotationData.getOrDefault("bus", EnumHolder(null, "FORGE")) as EnumHolder

            val busTarget = Bus.valueOf(busTargetHolder.value)

            if (mod.modId == modId && sides.contains(FMLEnvironment.dist)) {
                try {


                    val clazz = Class.forName(data.classType.className, true, loader)
                    val kclass = clazz.kotlin

                    val objectInstance = kclass.objectInstance
                    if (objectInstance != null) {
                        if (!hasStaticEventHandlers(clazz)) {
                            LOGGER.debug(
                                Logging.LOADING,
                                "Unsubscribing {} as Common Java Class from {}",
                                data.classType.className,
                                busTarget
                            )
                            busTarget.bus().get().unregister(data.classType.className)
                        }

                        if (hasObjectEventHandlers(clazz)) {
                            LOGGER.debug(
                                Logging.LOADING,
                                "Auto-subscribing Kotlin object {} to {}",
                                data.classType.className,
                                busTarget
                            )
                            busTarget.bus().get().register(objectInstance)
                        }
                    }
                } catch (e: ClassNotFoundException) {
                    LOGGER.fatal(
                        Logging.LOADING,
                        "Failed to load mod Kotlin class {} for @EventBusSubscriber annotation",
                        data.classType,
                        e
                    )
                    throw RuntimeException(e)
                }
            }
        }
    }

    private fun hasStaticEventHandlers(clazz: Class<*>): Boolean {
        return clazz.methods.any { it.isAnnotationPresent(SubscribeEvent::class.java) && Modifier.isStatic(it.modifiers) }
    }

    private fun hasObjectEventHandlers(clazz: Class<*>): Boolean {
        return clazz.methods.any { it.isAnnotationPresent(SubscribeEvent::class.java) && !Modifier.isStatic(it.modifiers) }
    }
}