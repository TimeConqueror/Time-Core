package ru.timeconqueror.timecore.api.registry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import ru.timeconqueror.timecore.api.TimeMod;
import ru.timeconqueror.timecore.api.client.resource.location.ItemModelLocation;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * All {@link TimeRegister}s are used to simplify stuff registering.
 * <p>
 * To use it you need to:
 * <ol>
 *     <li>Create its instance and declare it static. Access modifier can be any.</li>
 *     <li>Attach {@link AutoRegistrable} annotation to it to register it as an event listener.</li>
 *     <li>Extend you main mod class from {@link TimeMod} to enable TimeCore's annotations.</li>
 * </ol>
 *
 * <b>Features:</b>
 * If you need to register stuff,
 * your first step will be to call method {@link #register(String, EntityType.Builder)} or {@link #registerLiving(String, EntityType.Builder, Supplier)}
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
public class EntityRegister extends ForgeRegister<EntityType<?>> {
    private final ItemRegister itemRegister;

    public EntityRegister(String modid) {
        super(ForgeRegistries.ENTITIES, modid);
        itemRegister = new ItemRegister(modid);
    }

    /**
     * Adds type to the queue, all entries from which will be registered later.
     * <p>
     * This method also returns {@link EntityRegisterChain} to provide extra methods, which you can apply to entry being registered.
     * All methods of {@link EntityRegisterChain} are optional.
     *
     * @param attributes  entity attributes map with attributes as speed, max health, etc.
     * @param name        The item's name, will automatically have the modid as a namespace.
     * @param typeBuilder A builder for the entity type with provided settings.
     * @return A {@link EntityRegisterChain} for adding some extra stuff.
     * @see EntityRegisterChain
     */
    public <T extends LivingEntity> EntityRegisterChain<T> registerLiving(String name, EntityType.Builder<T> typeBuilder, Supplier<AttributeModifierMap> attributes) {
        EntityRegisterChain<T> chain = register(name, typeBuilder);

        runOnCommonSetup(() -> GlobalEntityTypeAttributes.put(chain.holder.get(), attributes.get()));

        return chain;
    }

    /**
     * Note: For {@link LivingEntity} use {@link #registerLiving(String, EntityType.Builder, Supplier)}
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
        EntityType<T> type = typeBuilder.build(getModId() + ":" + name);
        RegistryObject<EntityType<T>> holder = registerEntry(name, () -> type);

        return new EntityRegisterChain<>(holder, type);
    }

    @Override
    public void regToBus(IEventBus modEventBus) {
        super.regToBus(modEventBus);
        itemRegister.regToBus(modEventBus);
    }

    public class EntityRegisterChain<T extends Entity> extends RegisterChain<EntityType<T>> {
        private final EntityType<T> type;

        protected EntityRegisterChain(RegistryObject<EntityType<T>> holder, EntityType<T> type) {
            super(holder);
            this.type = type;
        }

        /**
         * Registers simple spawn egg ({@link SpawnEggItem}) with name {@code spawn_$entityName} with default properties.
         * Automatically adds default json model for it.
         *
         * @param primaryArgb   primary color
         * @param secondaryArgb secondary color
         * @param tab           creative tab where item will be placed
         */
        public EntityRegisterChain<T> spawnEgg(int primaryArgb, int secondaryArgb, ItemGroup tab) {
            return spawnEgg(primaryArgb, secondaryArgb, new Item.Properties().tab(tab));
        }

        /**
         * Registers simple spawn egg ({@link SpawnEggItem}) with name {@code spawn_$entityName}.
         * Automatically adds default json model for it.
         *
         * @param primaryArgb   primary color
         * @param secondaryArgb secondary color
         * @param properties    item properties
         */
        public EntityRegisterChain<T> spawnEgg(int primaryArgb, int secondaryArgb, Item.Properties properties) {
            return spawnEgg(getName() + "_spawn_egg", primaryArgb, secondaryArgb, properties);
        }

        /**
         * Registers simple spawn egg ({@link SpawnEggItem}) with provided name.
         * Automatically adds default json model for it.
         *
         * @param primaryArgb   primary color
         * @param secondaryArgb secondary color
         * @param properties    item properties
         */
        public EntityRegisterChain<T> spawnEgg(String name, int primaryArgb, int secondaryArgb, Item.Properties properties) {
            itemRegister.register(name, () -> new SpawnEggItem(retrieve(), primaryArgb, secondaryArgb, properties))
                    .model(new ItemModelLocation("minecraft", "template_spawn_egg"));

            return this;
        }

        /**
         * Registers the spawn egg for this entity.
         *
         * @param itemSupplier item factory, should return new item instance every time it's called.
         * @param itemSettings extra stuff, that you can do for that item, like generating item model.
         */
        public <I extends Item> EntityRegisterChain<T> spawnEgg(String name, Supplier<I> itemSupplier, Consumer<ItemRegister.ItemRegisterChain<I>> itemSettings) {
            ItemRegister.ItemRegisterChain<I> itemRegisterChain = itemRegister.register(name, itemSupplier);
            itemSettings.accept(itemRegisterChain);
            return this;
        }

        /**
         * Returns the bound {@link EntityType}
         */
        public EntityType<T> retrieve() {
            return type;
        }
    }
}
