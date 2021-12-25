package ru.timeconqueror.timecore.api.registry;

import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import ru.timeconqueror.timecore.api.registry.util.Promised;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * This is the register for {@link ConfiguredFeature}.
 * If you need to register features, use {@link SimpleForgeRegister}
 *///TODO [minecraft/WorldSettingsImport]: Error adding element: Spread too big: 6 > 4
public class ConfiguredFeatureRegister extends VanillaRegister<ConfiguredFeature<?, ?>> {
    private final List<FeatureInfo> featureInfoList = new ArrayList<>();

    public ConfiguredFeatureRegister(String modId) {
        super(modId, BuiltinRegistries.CONFIGURED_FEATURE);
    }

    public <I extends ConfiguredFeature<?, ?>> ConfiguredFeatureRegistryChain<I> register(String name, GenerationStep.Decoration genStage, Supplier<I> configuredFeatureSup) {
        Promised<I> promised = registerEntry(name, configuredFeatureSup);

        return new ConfiguredFeatureRegistryChain<>(promised, genStage);
    }

    @Override
    public void regToBus(IEventBus bus) {
        super.regToBus(bus);
        MinecraftForge.EVENT_BUS.addListener(this::onBiomeLoad);
    }

    private void onBiomeLoad(BiomeLoadingEvent event) {
        for (FeatureInfo info : featureInfoList) {
            if (info.biomePredicate.test(event)) {
                event.getGeneration().getFeatures(info.stage).add(info.promisedFeature::get);
            }
        }
    }

    public class ConfiguredFeatureRegistryChain<I extends ConfiguredFeature<?, ?>> extends RegisterChain<I> {
        private final FeatureInfo featureInfo;

        private ConfiguredFeatureRegistryChain(Promised<I> promised, GenerationStep.Decoration stage) {
            super(promised);

            featureInfo = new FeatureInfo(promised, stage);
            featureInfoList.add(featureInfo);
        }

        /**
         * Controls, if feature can be generated in provided biome.
         * By default it will be registered for all biomes.
         */
        public ConfiguredFeatureRegistryChain<I> setBiomePredicate(Predicate<BiomeLoadingEvent> biomePredicate) {
            this.featureInfo.biomePredicate = biomePredicate;
            return this;
        }
    }

    private static class FeatureInfo {
        private final Promised<? extends ConfiguredFeature<?, ?>> promisedFeature;
        private final GenerationStep.Decoration stage;

        public FeatureInfo(Promised<? extends ConfiguredFeature<?, ?>> promisedFeature, GenerationStep.Decoration stage) {
            this.promisedFeature = promisedFeature;
            this.stage = stage;
        }

        private Predicate<BiomeLoadingEvent> biomePredicate = event -> true;
    }
}
