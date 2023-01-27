package ru.timeconqueror.timecore.api.registry;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.TimeCoreAPI;
import ru.timeconqueror.timecore.api.client.resource.location.ItemModelLocation;
import ru.timeconqueror.timecore.api.devtools.gen.lang.LangGeneratorFacade;
import ru.timeconqueror.timecore.api.registry.base.TaskHolder;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.api.registry.util.Promised;
import ru.timeconqueror.timecore.api.util.EnvironmentUtils;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * All {@link TimeRegister}s are used to simplify stuff registering.
 * <p>
 * To use it you need to:
 * <ol>
 *     <li>Create its instance and declare it static. Access modifier can be any.</li>
 *     <li>Attach {@link AutoRegistrable} annotation to it to register it as an event listener.</li>
 *     <li>Call {@link TimeCoreAPI#setup(Object)} from your mod constructor to enable TimeCore's annotations.</li>
 * </ol>
 *
 * <b>Features:</b>
 * If you need to register stuff,
 * your first step will be to call method
 * {@link #registerMob(String, EntityType.Builder)},
 * {@link #registerLiving(String, EntityType.Builder)} or
 * {@link #register(String, EntityType.Builder)}
 * depending on what entity you need to register.
 * Since this register has any extra available registering stuff, these methods will return Register Chain,
 * which will have extra methods to apply.
 * <br>
 * <br>
 * <blockquote>
 *     <pre>
 *     public class EntityRegistryExample {
 *    {@literal @}AutoRegistrable
 *     private static final EntityRegister REGISTER = new EntityRegister(TimeCore.MODID);
 *
 *     public static final EntityType&lt;FloroEntity> FLORO = REGISTER.registerLiving("floro",
 *             EntityType.Builder.of(FloroEntity::new, EntityClassification.MONSTER)
 *                     .setTrackingRange(80)
 *                     .setShouldReceiveVelocityUpdates(true)
 *                     .sized(1, 2),
 *             () -> FloroEntity.createAttributes().build())
 *             .spawnEgg(0xFF00FF00, 0xFF000000, ItemGroup.TAB_MISC)// <- one of extra features
 *             .retrieve();// <- retrieving entity type from our register chain.
 *      }
 *     </pre>
 * </blockquote>
 * <br>
 * <p>
 * Examples can be seen at test module.
 */
public class EntityRegister extends VanillaRegister<EntityType<?>> {
    private final ItemRegister itemRegister;
    private final TaskHolder<Consumer<EntityAttributeCreationEvent>> entityAttributesEventRuns = TaskHolder.make(EntityAttributeCreationEvent.class);

    public EntityRegister(String modid) {
        super(ForgeRegistries.ENTITY_TYPES, modid);
        itemRegister = new ItemRegister(modid);
    }

    /**
     * Adds mob type to the queue, all entries from which will be registered later.
     * <p>
     * This method also returns {@link EntityRegisterChain} to provide extra methods, which you can apply to entry being registered.
     * All methods of {@link EntityRegisterChain} are optional.
     *
     * @param name        The item's name, will automatically have the modid as a namespace.
     * @param typeBuilder A builder for the entity type with provided settings.
     * @return A {@link EntityRegisterChain} for adding some extra stuff.
     * @see EntityRegisterChain
     */
    public <T extends Mob> MobRegisterChain<T> registerMob(String name, EntityType.Builder<T> typeBuilder) {
        return new MobRegisterChain<>(registerLiving(name, typeBuilder));
    }

    /**
     * Note: For {@link Mob} use {@link #registerMob(String, EntityType.Builder)}
     * <p>
     * Adds living type to the queue, all entries from which will be registered later.
     * <p>
     * This method also returns {@link EntityRegisterChain} to provide extra methods, which you can apply to entry being registered.
     * All methods of {@link EntityRegisterChain} are optional.
     *
     * @param name        The item's name, will automatically have the modid as a namespace.
     * @param typeBuilder A builder for the entity type with provided settings.
     * @return A {@link EntityRegisterChain} for adding some extra stuff.
     * @see EntityRegisterChain
     */
    public <T extends LivingEntity> LivingRegisterChain<T> registerLiving(String name, EntityType.Builder<T> typeBuilder) {
        EntityRegisterChain<T> chain = registerInternal(name, typeBuilder);
        return new LivingRegisterChain<>(chain);
    }

    /**
     * Note: For {@link Mob} use {@link #registerMob(String, EntityType.Builder)}
     * Note: For {@link LivingEntity} use {@link #registerLiving(String, EntityType.Builder)}
     * <p>
     * Adds type to the queue, all entries from which will be registered later.
     * <p>
     * This method also returns {@link EntityRegisterChain} to provide extra methods, which you can apply to entry being registered.
     * All methods of {@link EntityRegisterChain} are optional.
     *
     * @param name        The item's name, will automatically have the modid as a namespace.
     * @param typeBuilder A builder for the entity type with provided settings.
     * @return A {@link EntityRegisterChain} for adding some extra stuff.
     * @see EntityRegisterChain
     */
    public <T extends Entity> EntityRegisterChain<T> register(String name, EntityType.Builder<T> typeBuilder) {
        return registerInternal(name, typeBuilder);
    }

    private <T extends Entity> EntityType<T> build(String name, EntityType.Builder<T> builder) {
        return builder.build(getModId() + ":" + name);
    }

    private <T extends Entity> EntityRegisterChain<T> registerInternal(String name, EntityType.Builder<T> type) {
        Promised<EntityType<T>> holder = registerEntry(name, () -> build(name, type));

        return new EntityRegisterChain<T>(holder);
    }

    private void onEntityAttributeCreationEvent(EntityAttributeCreationEvent event) {
        entityAttributesEventRuns.doForEachAndRemove(consumer -> consumer.accept(event));
    }

    @Override
    public void regToBus(IEventBus modEventBus) {
        super.regToBus(modEventBus);
        itemRegister.regToBus(modEventBus);
        modEventBus.addListener(this::onEntityAttributeCreationEvent);
    }

    public class EntityRegisterChain<T extends Entity> extends RegisterChain<EntityType<T>> {
        protected EntityRegisterChain(Promised<EntityType<T>> holder) {
            super(holder);
        }
//TODO add enName for spawn eggs

        /**
         * Adds entity entry to {@link LangGeneratorFacade}, which will place all entries in en_us.json file upon {@link GatherDataEvent}.
         * Generator will generate entries only in {@code runData} launch mode.
         *
         * @param enName english name of item
         */
        public EntityRegisterChain<T> name(String enName) {
            if (EnvironmentUtils.isInDev()) {
                runAfterRegistering(() -> EntityRegister.this.getLangGeneratorFacade().addEntityEntry(asPromised().get(), enName));
            }
            return this;
        }
    }

    public class LivingRegisterChain<T extends LivingEntity> extends EntityRegisterChain<T> {
        protected LivingRegisterChain(EntityRegisterChain<T> ancestor) {
            super(ancestor.promise);
        }

        /**
         * Binds attribute map to the living type.
         * Required for every living entity, which {@link MobCategory} is not equal to {@link MobCategory#MISC}
         */
        public LivingRegisterChain<T> attributes(Supplier<AttributeSupplier> attributesSup) {
            entityAttributesEventRuns.add(e -> e.put(asPromised().get(), attributesSup.get()));
            return this;
        }
    }

    public class MobRegisterChain<T extends Mob> extends LivingRegisterChain<T> {
        protected MobRegisterChain(EntityRegisterChain<T> ancestor) {
            super(ancestor);
        }

        /**
         * Sets up settings for spawning mob in world naturally
         */
        public MobRegisterChain<T> spawnSettings(SpawnPlacements.Type spawnType, Heightmap.Types heightMapType, SpawnPlacements.SpawnPredicate<T> spawnPredicate) {
            runOnCommonSetup(() -> SpawnPlacements.register(asPromised().get(), spawnType, heightMapType, spawnPredicate));

            return this;
        }

        /**
         * Registers simple spawn egg ({@link ForgeSpawnEggItem}) with name {@code spawn_$entityName} with default properties.
         * Automatically adds default json model for it.
         *
         * @param primaryArgb   primary color
         * @param secondaryArgb secondary color
         * @param tab           creative tab where item will be placed
         */
        public MobRegisterChain<T> spawnEgg(int primaryArgb, int secondaryArgb, CreativeModeTab tab) {
            return spawnEgg(primaryArgb, secondaryArgb, tab, new Item.Properties());
        }

        /**
         * Registers simple spawn egg ({@link ForgeSpawnEggItem}) with name {@code spawn_$entityName}.
         * Automatically adds default json model for it.
         *
         * @param primaryArgb   primary color
         * @param secondaryArgb secondary color
         * @param tab           creative mod tab, which will hold the provided spawn egg.
         * @param properties    item properties
         */
        public MobRegisterChain<T> spawnEgg(int primaryArgb, int secondaryArgb, @Nullable CreativeModeTab tab, Item.Properties properties) {
            return spawnEgg(getName() + "_spawn_egg", primaryArgb, secondaryArgb, tab, properties);
        }

        /**
         * Registers simple spawn egg ({@link ForgeSpawnEggItem}) with provided name.
         * Automatically adds default json model for it.
         *
         * @param primaryArgb   primary color
         * @param secondaryArgb secondary color
         * @param tab           creative mod tab, which will hold the provided spawn egg.
         * @param properties    item properties
         */
        public MobRegisterChain<T> spawnEgg(String name, int primaryArgb, int secondaryArgb, @Nullable CreativeModeTab tab, Item.Properties properties) {
            //FIXME Forge, wtf, it's not threadsafe!
            ItemRegister.ItemRegisterChain<ForgeSpawnEggItem> chain = itemRegister
                    .register(name, () -> new ForgeSpawnEggItem(asPromised(), primaryArgb, secondaryArgb, properties))
                    .model(new ItemModelLocation("minecraft", "template_spawn_egg"));

            if(tab != null) {
                chain.tab(tab);
            }

            return this;
        }

        /**
         * Registers the spawn egg for this entity.
         *
         * @param itemSupplier item factory, should return new item instance every time it's called.
         * @param itemSettings extra stuff, that you can do for that item, like generating item model.
         */
        public <I extends Item> MobRegisterChain<T> spawnEgg(String name, Supplier<I> itemSupplier, Consumer<ItemRegister.ItemRegisterChain<I>> itemSettings) {
            ItemRegister.ItemRegisterChain<I> itemRegisterChain = itemRegister.register(name, itemSupplier);
            itemSettings.accept(itemRegisterChain);
            return this;
        }
    }
}
