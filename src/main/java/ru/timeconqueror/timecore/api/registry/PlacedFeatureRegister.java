//package ru.timeconqueror.timecore.api.registry;
//
//import net.minecraft.data.BuiltinRegistries;
//import net.minecraft.world.level.levelgen.GenerationStep;
//import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
//import net.minecraft.world.level.levelgen.feature.Feature;
//import net.minecraft.world.level.levelgen.placement.PlacedFeature;
//import net.minecraftforge.common.MinecraftForge;
//import net.minecraftforge.event.world.BiomeLoadingEvent;
//import net.minecraftforge.eventbus.api.IEventBus;
//import ru.timeconqueror.timecore.api.registry.util.Promised;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.function.Predicate;
//import java.util.function.Supplier;
//
///**
// * This is the register for {@link PlacedFeature}.
// * PlacedFeature is a ConfiguredFeature + 0 or more Placement Modifiers that tells the
// * ConfiguredFeature where and how often it should generate in chunks.
// * <p>
// * If you need to register {@link Feature}, use {@link SimpleForgeRegister}
// * If you need to register {@link ConfiguredFeature, use {@link SimpleVanillaRegister} (//TODO [minecraft/WorldSettingsImport]: Error adding element: Spread too big: 6 > 4)
// */
//public class PlacedFeatureRegister extends VanillaRegister<PlacedFeature> {
//    private final List<Info> featureInfoList = new ArrayList<>();
//
//    public PlacedFeatureRegister(String modId) {
//        super(modId, BuiltinRegistries.PLACED_FEATURE);
//    }
//
//    public <I extends PlacedFeature> ConfiguredFeatureRegistryChain<I> register(String name, GenerationStep.Decoration genStage, Supplier<I> configuredFeatureSup) {
//        Promised<I> promised = registerEntry(name, configuredFeatureSup);
//
//        return new ConfiguredFeatureRegistryChain<>(promised, genStage);
//    }
//
//    @Override
//    public void regToBus(IEventBus bus) {
//        super.regToBus(bus);
//        MinecraftForge.EVENT_BUS.addListener(this::onBiomeLoad);
//    }
//
//    private void onBiomeLoad(BiomeLoadingEvent event) {
//        for (Info info : featureInfoList) {
//            if (info.biomePredicate.test(event)) {
//                event.getGeneration().addFeature(info.stage.ordinal(), info.promisedFeature::get);
//            }
//        }
//    }
//
//    public class ConfiguredFeatureRegistryChain<I extends PlacedFeature> extends RegisterChain<I> {
//        private final Info info;
//
//        private ConfiguredFeatureRegistryChain(Promised<I> promised, GenerationStep.Decoration stage) {
//            super(promised);
//
//            info = new Info(promised, stage);
//            featureInfoList.add(info);
//        }
//
//        /**
//         * Controls, if feature can be generated in provided biome.
//         * By default, it will be registered for all biomes.
//         */
//        public ConfiguredFeatureRegistryChain<I> allowedInBiomes(Predicate<BiomeLoadingEvent> biomePredicate) {
//            this.info.biomePredicate = biomePredicate;
//            return this;
//        }
//    }
//
//    private static class Info {
//        private final Promised<? extends PlacedFeature> promisedFeature;
//        private final GenerationStep.Decoration stage;
//
//        public Info(Promised<? extends PlacedFeature> promisedFeature, GenerationStep.Decoration stage) {
//            this.promisedFeature = promisedFeature;
//            this.stage = stage;
//        }
//
//        private Predicate<BiomeLoadingEvent> biomePredicate = event -> true;
//    }
//}
