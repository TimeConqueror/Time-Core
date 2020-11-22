package ru.timeconqueror.timecore;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import ru.timeconqueror.timecore.api.TimeMod;
import ru.timeconqueror.timecore.devtools.StructureRevealer;
import ru.timeconqueror.timecore.util.reflection.ReflectionHelper;

@Mod(TimeCore.MODID)
public final class TimeCore implements TimeMod {
    public static final String MODID = "timecore";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static TimeCore INSTANCE = null;

    public TimeCore() {
        INSTANCE = this;

        checkForMixinBootstrap();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, this::test2);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::test1);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, this::test3);
    }

    /**
     * Creates ResourceLocation with bound mod id.
     */
    public static ResourceLocation rl(String path) {
        return new ResourceLocation(MODID, path);
    }

    private void setup(final FMLCommonSetupEvent event) {
        ReflectionHelper.initClass(StructureRevealer.class);
    }

    public void test1(ModelRegistryEvent event) {
        System.out.println("ModelRegistryEvent");
    }

    public void test2(RegistryEvent.Register<Item> event) {
        System.out.println("RegistryEvent.Register<Item>");
    }

    public void test3(RegistryEvent.Register<Block> event) {
        System.out.println("RegistryEvent.Register<Block>");
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
    }

    private static void checkForMixinBootstrap() {
        try {
            if (MixinEnvironment.getCurrentEnvironment().getPhase() != MixinEnvironment.Phase.DEFAULT) {
                throw new IllegalArgumentException("Mixins are not initialized ");
            }
        } catch (NoClassDefFoundError e) {
            throw new IllegalStateException("TimeCore requires MixinBootstrap Mod to be loaded.");
        }
    }

}
