package ru.timeconqueror.timecore.api.registry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.storage.StructureTags;
import ru.timeconqueror.timecore.api.util.Pair;
import ru.timeconqueror.timecore.api.util.Temporal;
import ru.timeconqueror.timecore.mixins.accessor.ChunkGeneratorAccessor;
import ru.timeconqueror.timecore.mixins.accessor.StructureSettingsAccessor;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class StructureFeatureRegister extends ForgeRegister<StructureFeature<?>> {
    private final List<StructureInfo<?, ?>> structureInfoList = new ArrayList<>();

    public StructureFeatureRegister(String modid) {
        super(ForgeRegistries.STRUCTURE_FEATURES, modid);
    }

    /**
     * Adds entry in provided {@code structureSup} to the queue, all entries from which will be registered later.
     * <p>
     * This method also returns {@link StructureRegisterChain} to provide extra methods, which you can apply to entry being registered.
     * All methods of {@link StructureRegisterChain} are optional.
     *
     * @param name               The block's name, will automatically have the modid as a namespace.
     * @param structureSup       A factory for the new structure, it should return a new instance every time it is called.
     * @param separationSettings separation settings for provided structure
     * @param configCodec        codec for provided structure
     * @param featureConfig      config for feature representation of this structure
     * @return A {@link StructureRegisterChain} for adding some extra stuff.
     * @see StructureRegisterChain
     */
    public <T extends FeatureConfiguration, S extends StructureFeature<T>> StructureRegisterChain<T, S> register(String name, Function<Codec<T>, S> structureSup, TimeStructureSeparationSettings separationSettings, Codec<T> configCodec, T featureConfig) {
        RegistryObject<S> holder = this.registerEntry(name, () -> structureSup.apply(configCodec));
        return new StructureRegisterChain<>(holder, separationSettings.toVanilla(holder.getId()), featureConfig);
    }

    /**
     * Adds entry in provided {@code structureSup} to the queue, all entries from which will be registered later.
     * <p>
     * This method also returns {@link StructureRegisterChain} to provide extra methods, which you can apply to entry being registered.
     * All methods of {@link StructureRegisterChain} are optional.
     *
     * @param name               The block's name, will automatically have the modid as a namespace.
     * @param structureSup       A factory for the new structure, it should return a new instance every time it is called.
     * @param separationSettings separation settings for provided structure
     * @param configCodec        codec for provided structure
     * @param featureConfig      config for feature representation of this structure
     * @return A {@link StructureRegisterChain} for adding some extra stuff.
     * @see StructureRegisterChain
     */
    public <T extends FeatureConfiguration, S extends StructureFeature<T>> StructureRegisterChain<T, S> register(String name, Function<Codec<T>, S> structureSup, StructureFeatureConfiguration separationSettings, Codec<T> configCodec, T featureConfig) {
        RegistryObject<S> holder = this.registerEntry(name, () -> structureSup.apply(configCodec));
        return new StructureRegisterChain<>(holder, separationSettings, featureConfig);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void validateEntry(StructureFeature<?> entry) {
        super.validateEntry(entry);

        if (entry.step() == null) {
            throw new IllegalStateException(String.format("Structure '%s' should override #step method (Default implementation works only for vanilla structures. Define the step there yourselves.)", entry.getRegistryName()));
        }
    }

    @Override
    public void regToBus(IEventBus bus) {
        super.regToBus(bus);

        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(this::onWorldLoad);
    }

    protected void onCommonSetup(FMLCommonSetupEvent event) {
        enqueueWork(event, () -> {
            ImmutableList.Builder<StructureFeature<?>> noiseAffectedFeatures = ImmutableList.builder();

            for (StructureInfo<?, ?> info : structureInfoList) {
                StructureFeature<?> structure = info.structureFeature();

                /*
                 *Whether surrounding land will be modified automatically to conform to the bottom of the structure.
                 * Basically, it adds land at the base of the structure like it does for Villages and Outposts.
                 * Doesn't work well on structure that have pieces stacked vertically or change in heights.
                 *
                 * Note: The air space this method will create will be filled with water if the structure is below sealevel.
                 * This means this is best for structure above sealevel so keep that in mind.
                 */
                StructureFeature.STRUCTURES_REGISTRY.put(structure.getRegistryName().toString(), structure);

                if (info.transformSurroundingLand) {
                    noiseAffectedFeatures.add(structure);
                }
            }

            StructureFeature.NOISE_AFFECTING_FEATURES = noiseAffectedFeatures.addAll(StructureFeature.NOISE_AFFECTING_FEATURES).build();

            /*
             * This is the map that holds the default spacing of all structures.
             * Always add your structure to here so that other mods can utilize it if needed.
             *
             * However, while it does propagate the spacing to some correct dimensions from this map,
             * it seems it doesn't always work for code made dimensions as they read from this list beforehand.
             *
             * Instead, we will use the WorldEvent.Load event to add the structure
             * spacing from this list into that dimension or to do dimension blacklisting properly.
             */
            StructureSettings.DEFAULTS =
                    ImmutableMap.<StructureFeature<?>, StructureFeatureConfiguration>builder()
                            .putAll(StructureSettings.DEFAULTS)
                            .putAll(structureInfoList.stream()
                                    .collect(Collectors.<StructureInfo<?, ?>, StructureFeature<?>, StructureFeatureConfiguration>
                                            toMap(StructureInfo::structureFeature, structureInfo -> structureInfo.separationSettings
                                    )))
                            .build();

            /*
             * There are very few mods that relies on seeing your structure in the noise settings registry before the world is made.
             *
             * You may see some mods add their spacings to DimensionSettings.BUILTIN_OVERWORLD instead of the NOISE_GENERATOR_SETTINGS loop below but
             * that field only applies for the default overworld and won't add to other worldtypes or dimensions (like amplified or Nether).
             * So yeah, don't do DimensionSettings.BUILTIN_OVERWORLD. Use the NOISE_GENERATOR_SETTINGS loop below instead if you must.
             */
            BuiltinRegistries.NOISE_GENERATOR_SETTINGS.entrySet().forEach(settings -> {
                StructureSettings structureSettings = settings.getValue().structureSettings();

                /*
                 * Pre-caution in case a mod makes the structure map immutable like datapacks do.
                 * I take no chances myself. You never know what another mods does...
                 */
                Map<StructureFeature<?>, StructureFeatureConfiguration> configurationMap = structureSettings.structureConfig();
                if (configurationMap instanceof ImmutableMap) {
                    structureSettings.structureConfig = new HashMap<>(configurationMap);
                }

                for (StructureInfo<?, ?> info : structureInfoList) {
                    structureSettings.structureConfig.put(info.structureFeature(), info.separationSettings);
                }
            });

            for (StructureInfo<?, ?> info : structureInfoList) {
                info.setFeatureReadyToLoad();
                Registry.register(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, new ResourceLocation(getModId(), "configured_" + info.regObject().getId().getPath()), info.configuredStructureFeature());

                info.tags.doAndRemove(tags -> StructureTags.put(tags, info.structureFeature()));
            }
        });

        super.onCommonSetup(event);
    }

    private void onWorldLoad(final WorldEvent.Load event) {
        if (event.getWorld() instanceof ServerLevel level) {
            ChunkGenerator chunkGenerator = level.getChunkSource().getGenerator();

            // Skip superflat worlds to prevent issues with it. Plus, users don't want structures clogging up their superflat worlds.
            if (chunkGenerator instanceof FlatLevelSource && level.dimension().equals(Level.OVERWORLD)) {
                return;
            }

            StructureSettings worldStructureConfig = chunkGenerator.getSettings();

            //////////// BIOME BASED STRUCTURE SPAWNING ////////////
            /*
             * NOTE: BiomeLoadingEvent from Forge API does not work with structures anymore.
             * Instead, we will use the below to add our structure to overworld biomes.
             * Remember, this is temporary until Forge API finds a better solution for adding structures to biomes.
             */

            HashMap<StructureFeature<?>, HashMultimap<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>>> structureTable = new HashMap<>();

            //TODO measure time, maybe make the loop parallel
            //TODO add statistics (how many biomes satisfied predicate)
            for (StructureInfo<?, ?> info : structureInfoList) {
                if (info.dimensionPredicate.test(level)) {
                    for (Map.Entry<ResourceKey<Biome>, Biome> biomeEntry : level.registryAccess().ownedRegistryOrThrow(Registry.BIOME_REGISTRY).entrySet()) {
                        if (info.biomePredicate.test(biomeEntry.getKey(), biomeEntry.getValue())) {
                            associateBiomeToConfiguredStructure(structureTable, info.feature.get(), biomeEntry.getKey());
                        }
                    }
                }
            }

            ImmutableMap<StructureFeature<?>, ImmutableMultimap<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>>> oldConfiguredStructures = ((StructureSettingsAccessor) worldStructureConfig).getConfiguredStructures();

            for (Map.Entry<StructureFeature<?>, ImmutableMultimap<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>>> e : oldConfiguredStructures.entrySet()) {
                HashMultimap<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>> configuredStructureBiomes = structureTable.get(e.getKey());
                if (configuredStructureBiomes != null) {
                    configuredStructureBiomes.putAll(e.getValue());
                }
            }

            ((StructureSettingsAccessor) worldStructureConfig).setConfiguredStructures(
                    structureTable.entrySet().stream()
                            .map(e -> {
                                ImmutableMultimap<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>> configuredStructureBiomes = ImmutableMultimap.<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>>builder()
                                        .putAll(e.getValue())
                                        .build();

                                return Pair.<StructureFeature<?>, ImmutableMultimap<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>>>of(e.getKey(), configuredStructureBiomes);
                            })
                            .collect(Pair.toImmutableMap())
            );

            //////////// DIMENSION BASED STRUCTURE SPAWNING ////////////
            /*
             * Skip Terraforged's chunk generator as they are a special case of a mod locking down their chunkgenerator.
             * They will handle your structure spacing for your if you add to BuiltinRegistries.NOISE_GENERATOR_SETTINGS in your structure's registration.
             */
            Codec<? extends ChunkGenerator> codec = ((ChunkGeneratorAccessor) chunkGenerator).callCodec();
            ResourceLocation cgRL = Registry.CHUNK_GENERATOR.getKey(codec);
            if (cgRL != null && cgRL.getNamespace().equals("terraforged")) return;

            Map<StructureFeature<?>, StructureFeatureConfiguration> newStructureConfig = new HashMap<>(worldStructureConfig.structureConfig());
            for (StructureInfo<?, ?> info : structureInfoList) {
                if (!info.dimensionPredicate.test(level)) {
                    newStructureConfig.remove(info.structureFeature());//TODO check if I really need to remove it???
                    continue;
                }
                /*
                 * putIfAbsent so people can override the spacing with dimension datapacks themselves if they wish to customize spacing more precisely per dimension.//TODO per dimension spacing
                 *
                 * NOTE: if you add per-dimension spacing configs, you can't use putIfAbsent as BuiltinRegistries.NOISE_GENERATOR_SETTINGS in FMLCommonSetupEvent
                 * already added your default structure spacing to some dimensions. You would need to override the spacing with .put(...)
                 * And if you want to do dimension blacklisting, you need to remove the spacing entry entirely from the map below to prevent generation safely.
                 */
                newStructureConfig.putIfAbsent(info.structureFeature(), StructureSettings.DEFAULTS.get(info.structureFeature()));
            }
            worldStructureConfig.structureConfig = newStructureConfig;
        }
    }

    /**
     * Helper method that handles setting up the map to multimap relationship to help prevent issues.
     */
    private static void associateBiomeToConfiguredStructure(Map<StructureFeature<?>, HashMultimap<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>>> structureMultimap, ConfiguredStructureFeature<?, ?> configuredStructureFeature, ResourceKey<Biome> biomeRegistryKey) {
        structureMultimap.putIfAbsent(configuredStructureFeature.feature, HashMultimap.create());
        HashMultimap<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>> configuredToBiomeBindings = structureMultimap.get(configuredStructureFeature.feature);
        if (configuredToBiomeBindings.containsValue(biomeRegistryKey)) {
            TimeCore.LOGGER.error(
                    """
                                Detected 2 ConfiguredStructureFeatures that share the same base StructureFeature trying to be added to same biome. One will be prevented from spawning.
                                This issue happens with vanilla too and is why a Snowy Village and Plains Village cannot spawn in the same biome because they both use the Village base structure.
                                The two conflicting ConfiguredStructures are: {}, {}
                                The biome that is attempting to be shared: {}
                            """,
                    BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(configuredStructureFeature),
                    BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(configuredToBiomeBindings.entries().stream().filter(e -> e.getValue() == biomeRegistryKey).findFirst().get().getKey()),
                    biomeRegistryKey
            );
        } else {
            configuredToBiomeBindings.put(configuredStructureFeature, biomeRegistryKey);
        }
    }

    public class StructureRegisterChain<T extends FeatureConfiguration, S extends StructureFeature<T>> extends RegisterChain<S> {
        private final StructureHolder<T, S> holder;
        private final StructureInfo<T, S> info;

        protected StructureRegisterChain(RegistryObject<S> regObj, StructureFeatureConfiguration separationSettings, T featureConfig) {
            super(regObj);

            this.info = new StructureInfo<>(regObj, separationSettings, s -> s.configured(featureConfig));
            this.holder = new StructureHolder<>(regObj, info::configuredStructureFeature);

            structureInfoList.add(info);
        }

        /**
         * Whether surrounding land will be modified automatically to conform to the bottom of the structure.
         * Basically, it adds land at the base of the structure like it does for Villages and Outposts.
         * Doesn't work well on structure that have pieces stacked vertically or change in heights.
         * <p>
         * The air space this method will create will be filled with water if the structure is below sealevel.
         * This means this is best for structure above sealevel so keep that in mind.
         */
        public StructureRegisterChain<T, S> transformsSurroundingLand() {
            this.info.transformSurroundingLand = true;
            return this;
        }

        /**
         * Controls, if feature can be generated in provided world.
         * By default it will be registered for all dimensions.
         */
        public StructureRegisterChain<T, S> allowedInDimensions(Predicate<ServerLevel> worldPredicate) {
            this.info.dimensionPredicate = worldPredicate;
            return this;
        }

        /**
         * Controls, if feature can be generated in provided biome.
         * By default it will be registered for all biomes.
         */
        public StructureRegisterChain<T, S> allowedInBiomes(BiPredicate<ResourceKey<Biome>, Biome> biomePredicate) {
            this.info.biomePredicate = biomePredicate;
            return this;
        }

        /**
         * Applies some structure settings.
         */
        public StructureRegisterChain<T, S> tagged(StructureTags.Tag tag) {
            this.info.tags.get().add(tag);
            return this;
        }

        /**
         * Returns holder, from which you can get both structure and structure feature.
         * Keep in mind, that structure feature will this registry name: "${your_mod_id}:configured_${structure_name}".
         * Example: "best_mod:configured_my_test_structure".
         */
        public StructureHolder<T, S> asHolder() {
            return holder;
        }
    }

    /**
     * Used for auto-generation of structure salt depending on hashcode of its registry key
     */
    public static class TimeStructureSeparationSettings {
        private final int spacing;
        private final int separation;

        private TimeStructureSeparationSettings(int spacing, int separation) {
            this.spacing = spacing;
            this.separation = separation;
        }

        /**
         * Creates new instance of TimeStructureSeparationSettings
         *
         * @param spacing    average distance apart in chunks between spawn attempts
         * @param separation minimum distance apart in chunks between spawn attempts
         */
        public static TimeStructureSeparationSettings create(int spacing, int separation) {
            if (spacing <= separation)
                throw new IllegalArgumentException("Spacing should be strictly higher than separation. ");
            return new TimeStructureSeparationSettings(spacing, separation);
        }

        private StructureFeatureConfiguration toVanilla(ResourceLocation structureName) {
            return new StructureFeatureConfiguration(spacing, separation, structureName.hashCode());
        }
    }

    public static class StructureHolder<T extends FeatureConfiguration, S extends StructureFeature<T>> {
        private final RegistryObject<S> registryObject;
        private final Supplier<ConfiguredStructureFeature<T, ? extends StructureFeature<T>>> structureFeature;

        public StructureHolder(RegistryObject<S> registryObject, Supplier<ConfiguredStructureFeature<T, ? extends StructureFeature<T>>> structureFeature) {
            this.registryObject = registryObject;
            this.structureFeature = structureFeature;
        }

        public RegistryObject<S> getRegistryObject() {
            return registryObject;
        }

        public S getStructure() {
            return registryObject.get();
        }

        public ConfiguredStructureFeature<T, ? extends StructureFeature<T>> getFeature() {
            return structureFeature.get();
        }
    }

    public static class StructureInfo<T extends FeatureConfiguration, S extends StructureFeature<T>> {
        private final RegistryObject<S> registryObject;
        private final Lazy<ConfiguredStructureFeature<T, ? extends StructureFeature<T>>> feature;
        private boolean featureReadyToLoad;
        private final StructureFeatureConfiguration separationSettings;
        private final Temporal<EnumSet<StructureTags.Tag>> tags = Temporal.of(EnumSet.noneOf(StructureTags.Tag.class));

        /**
         * Will add land at the base of the structure like it does for Villages and Outposts.
         * Doesn't work well on structure that have pieces stacked vertically or change in heights.
         */
        private boolean transformSurroundingLand;

        private BiPredicate<ResourceKey<Biome>, Biome> biomePredicate = (key, biome) -> true;
        private Predicate<ServerLevel> dimensionPredicate = event -> true;

        public StructureInfo(RegistryObject<S> registryObject, StructureFeatureConfiguration separationSettings, Function<S, ConfiguredStructureFeature<T, ? extends StructureFeature<T>>> structureFeatureFactory) {
            this.registryObject = registryObject;
            this.separationSettings = separationSettings;
            this.feature = () -> structureFeatureFactory.apply(registryObject.get());
        }

        public RegistryObject<S> regObject() {
            return registryObject;
        }

        public S structureFeature() {
            return registryObject.get();
        }

        private void setFeatureReadyToLoad() {
            this.featureReadyToLoad = true;
        }

        public ConfiguredStructureFeature<T, ? extends StructureFeature<T>> configuredStructureFeature() {
            if (!featureReadyToLoad) throw new IllegalStateException("Structure Feature hasn't been loaded yet");
            return feature.get();
        }
    }
}
