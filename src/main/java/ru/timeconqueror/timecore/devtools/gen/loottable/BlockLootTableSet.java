package ru.timeconqueror.timecore.devtools.gen.loottable;

import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.*;
import net.minecraft.loot.functions.ApplyBonus;
import net.minecraft.loot.functions.CopyName;
import net.minecraft.loot.functions.ExplosionDecay;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.state.Property;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.IStringSerializable;

import java.util.function.Function;

public abstract class BlockLootTableSet extends LootTableSet {
    protected static final ILootCondition.IBuilder SILK_TOUCH = MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.IntBound.atLeast(1))));
    protected static final ILootCondition.IBuilder NO_SILK_TOUCH = SILK_TOUCH.invert();
    protected static final ILootCondition.IBuilder SHEARS = MatchTool.toolMatches(ItemPredicate.Builder.item().of(Items.SHEARS));
    protected static final ILootCondition.IBuilder SILK_TOUCH_OR_SHEARS = SHEARS.or(SILK_TOUCH);
    protected static final ILootCondition.IBuilder NOT_SILK_TOUCH_OR_SHEARS = SILK_TOUCH_OR_SHEARS.invert();

    /**
     * Removes some items from a stack, if there was an explosion. Each item has a chance of 1/explosion radius to be lost.
     */
    protected static <T> T applyExplosionDecay(ILootFunctionConsumer<T> lootFunctionConsumer) {
        return lootFunctionConsumer.apply(ExplosionDecay.explosionDecay());
    }

    /**
     * Simply passes if the item would survive an explosion.
     * If there was no explosion, this condition always passes.
     * If there was an explosion, the chance of passing depends on the distance to the explosion and the explosion radius.
     */
    protected static <T> T applyPartiallySurvivingOnExplosion(ILootConditionConsumer<T> conditionConsumer) {
        return conditionConsumer.when(SurvivesExplosion.survivesExplosion());
    }

    /**
     * Creates a table, where the provided {@code drop} will be dropped with amount 1.
     *
     * @param drop item to drop
     */
    protected static LootTable.Builder createSingleItemTable(IItemProvider drop) {
        return LootTable.lootTable()
                .withPool(applyPartiallySurvivingOnExplosion(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(drop))));
    }

    /**
     * Creates a table, the provided {@code defaultDrop} will be dropped with amount 1, when provided {@code condition} is satisfied.
     * Otherwise will use provided {@code alternative}.
     *
     * @param defaultDrop block to drop
     * @param condition   condition, on which {@code defaultDrop} will be dropped
     * @param alternative alternative entry on {@code condition} fail
     */
    protected static LootTable.Builder createSelfDropDispatchTable(Block defaultDrop, ILootCondition.IBuilder condition, LootEntry.Builder<?> alternative) {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(defaultDrop).when(condition).otherwise(alternative)));
    }

    /**
     * Creates a table, where the provided {@code silkTouchDrop} will be dropped with amount 1, when is collected with silk touch enchantment.
     * Otherwise will use provided {@code alternative}.
     *
     * @param silkTouchDrop block to drop when silk touch is applied
     * @param alternative   alternative entry when collected without silk touch
     */
    protected static LootTable.Builder createSilkTouchDispatchTable(Block silkTouchDrop, LootEntry.Builder<?> alternative) {
        return createSelfDropDispatchTable(silkTouchDrop, SILK_TOUCH, alternative);
    }

    /**
     * Creates a table, where the provided {@code dropOnShearsCut} will be dropped with amount 1, when is collected with shears.
     * Otherwise will use provided {@code noShearAlternativeEntry}.
     *
     * @param dropOnShearsCut         block to drop when shears are applied
     * @param noShearAlternativeEntry alternative entry when collected without shears
     */
    protected static LootTable.Builder createOnShearsCutDispatchTable(Block dropOnShearsCut, LootEntry.Builder<?> noShearAlternativeEntry) {
        return createSelfDropDispatchTable(dropOnShearsCut, SHEARS, noShearAlternativeEntry);
    }

    /**
     * Creates a table, where the provided {@code dropOnSilkTouchOrShears} will be dropped with amount 1, when is collected with shears or silk touch enchantment.
     * Otherwise will use provided {@code alternative}.
     *
     * @param dropOnSilkTouchOrShears block to drop when shears or silk touch are applied
     * @param alternative             alternative entry when collected without shears or silk touch
     */
    protected static LootTable.Builder createSilkTouchOrShearsCutDispatchTable(Block dropOnSilkTouchOrShears, LootEntry.Builder<?> alternative) {
        return createSelfDropDispatchTable(dropOnSilkTouchOrShears, SILK_TOUCH_OR_SHEARS, alternative);
    }

    /**
     * Creates a table, where the provided {@code silkTouchDrop} will be dropped with amount 1, when is collected with shears or silk touch enchantment.
     * Otherwise will drop provided {@code dropWithoutSilkTouch} with amount 1.
     *
     * @param silkTouchDrop        block to drop when shears or silk touch are applied
     * @param dropWithoutSilkTouch alternative drop on collecting without shears or silk touch
     */
    protected static LootTable.Builder createSilkTouchDispatchTable(Block silkTouchDrop, IItemProvider dropWithoutSilkTouch) {
        return createSilkTouchDispatchTable(silkTouchDrop, applyPartiallySurvivingOnExplosion(ItemLootEntry.lootTableItem(dropWithoutSilkTouch)));
    }

    /**
     * Creates a table, where the provided {@code drop} will be dropped with randomly chosen amount from provided {@code range}
     *
     * @param drop  something to drop
     * @param range range, from which random amount of {@code drop} will be chosen
     */
    protected static LootTable.Builder createSingleItemTable(IItemProvider drop, IRandomRange range) {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(applyExplosionDecay(ItemLootEntry.lootTableItem(drop).apply(SetCount.setCount(range)))));
    }

    /**
     * Creates a table, where the provided {@code silkTouchDrop} will be dropped with amount 1, when is collected with silk touch enchantment.
     * Otherwise will drop provided {@code alternativeDrop} with provided {@code alternativeRange}.
     *
     * @param silkTouchDrop    block to drop when silk touch is applied
     * @param alternativeDrop  drop on collecting without silk touch
     * @param alternativeRange range, from which random amount of {@code alternativeDrop} will be chosen
     */
    protected static LootTable.Builder createSilkTouchDispatchTable(Block silkTouchDrop, IItemProvider alternativeDrop, IRandomRange alternativeRange) {
        return createSilkTouchDispatchTable(silkTouchDrop, applyExplosionDecay(ItemLootEntry.lootTableItem(alternativeDrop).apply(SetCount.setCount(alternativeRange))));
    }

    /**
     * Creates a table, where the provided {@code silkTouchDrop} will be dropped with amount 1, when is collected with silk touch enchantment.
     * Otherwise will drop nothing.
     *
     * @param dropOnSilkTouch block to drop when silk touch is applied
     */
    protected static LootTable.Builder createSilkTouchOnlyTable(IItemProvider dropOnSilkTouch) {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool().when(SILK_TOUCH).setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(dropOnSilkTouch)));
    }

    /**
     * Creates a table, where the provided {@code flower} and {@link Blocks#FLOWER_POT} will be dropped with amount 1.
     *
     * @param flower flower to drop
     */
    protected static LootTable.Builder createPotAndFlowerTable(IItemProvider flower) {
        return LootTable.lootTable()
                .withPool(applyPartiallySurvivingOnExplosion(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(Blocks.FLOWER_POT))))
                .withPool(applyPartiallySurvivingOnExplosion(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(flower))));
    }

    /**
     * Creates a table, where the provided {@code slab} will be dropped with amount 1 or 2 depending on {@link SlabBlock#TYPE} property.
     *
     * @param slab slab to drop
     */
    protected static LootTable.Builder createSlabItemTable(Block slab) {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1))
                        .add(applyExplosionDecay(ItemLootEntry.lootTableItem(slab).apply(
                                SetCount.setCount(ConstantRange.exactly(2)).when(BlockStateProperty.hasBlockStateProperties(slab).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SlabBlock.TYPE, SlabType.DOUBLE)))))));
    }

    /**
     * Creates a table, where the provided {@code drop} will be dropped with amount 1 when provided {@code propertyIn} has specific {@code value}.
     * Otherwise will drop nothing.
     *
     * @param drop       thing to drop
     * @param propertyIn property, which value should be checked for equality
     * @param value      of provided {@code propertyIn} which will be checked for equality
     */
    protected static <T extends Comparable<T> & IStringSerializable> LootTable.Builder createSinglePropConditionTable(Block drop, Property<T> propertyIn, T value) {
        return LootTable.lootTable()
                .withPool(applyPartiallySurvivingOnExplosion(LootPool.lootPool().setRolls(ConstantRange.exactly(1))
                        .add(ItemLootEntry.lootTableItem(drop).when(BlockStateProperty.hasBlockStateProperties(drop).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(propertyIn, value))))));
    }

    /**
     * Default method for handling drops from common tile entity.
     * <p>
     * Creates a table, where the provided {@code drop} will be dropped with amount 1.
     * If this block has a custom location, than the resulting item stack will also have it.
     *
     * @param drop block to drop
     */
    protected static LootTable.Builder createNameableTileEntityTable(Block drop) {
        return LootTable.lootTable()
                .withPool(applyPartiallySurvivingOnExplosion(LootPool.lootPool().setRolls(ConstantRange.exactly(1))
                        .add(ItemLootEntry.lootTableItem(drop).apply(CopyName.copyName(CopyName.Source.BLOCK_ENTITY)))));
    }

    /**
     * Creates a table, where the provided {@code dropWithShears} will be dropped with amount 1, when is collected with shears.
     * Otherwise will drop nothing.
     *
     * @param dropWithShears block to drop when shears are applied
     */
    protected static LootTable.Builder createShearsOnlyTable(IItemProvider dropWithShears) {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1))
                        .when(SHEARS).add(ItemLootEntry.lootTableItem(dropWithShears)));
    }

    /**
     * Used for all leaves.
     * Drops provided {@code silkTouchDrop} if with silk touch, otherwise drops the provided {@code alternativeDrop} with the passed {@code secondDropChances}
     * for fortune levels.
     */
    protected static LootTable.Builder createDropTableForLeaves(Block silkTouchDrop, Block alternativeDrop, float... secondDropChances) {
        return createSilkTouchOrShearsCutDispatchTable(silkTouchDrop, applyPartiallySurvivingOnExplosion(ItemLootEntry.lootTableItem(alternativeDrop)).when(TableBonus.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, secondDropChances)))
                .withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1))
                        .when(NOT_SILK_TOUCH_OR_SHEARS).add(applyExplosionDecay(ItemLootEntry.lootTableItem(Items.STICK)
                                .apply(SetCount.setCount(RandomValueRange.between(1.0F, 2.0F))))
                                .when(TableBonus.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F))));
    }

    /**
     * Drops {@code dropAlways} item, and the second {@code rareDrop} item plus more of the {@code dropAlways} items
     * when the loot condition is met,
     * applying fortune to only the second argument.
     */
    protected static LootTable.Builder createDropWithBonusItemTable(Item dropAlways, Item rareDrop, ILootCondition.IBuilder conditionBuilder) {
        return applyExplosionDecay(LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(ItemLootEntry.lootTableItem(dropAlways)
                                .when(conditionBuilder)
                                .otherwise(ItemLootEntry.lootTableItem(rareDrop))))
                .withPool(LootPool.lootPool().when(conditionBuilder)
                        .add(ItemLootEntry.lootTableItem(rareDrop)
                                .apply(ApplyBonus.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5714286F, 3)))));
    }

    public static LootTable.Builder noDrop() {
        return LootTable.lootTable();
    }

    @Override
    public LootParameterSet getParameterSet() {
        return LootParameterSets.BLOCK;
    }

    public void registerFlowerPotDrop(Block pot) {
        registerLootTable(pot, (flowerPot) -> createPotAndFlowerTable(((FlowerPotBlock) flowerPot).getContent()));
    }

    public void registerDropsOtherWhenSilkTouch(Block block, Block silkTouchDrop) {
        registerLootTable(block, createSilkTouchOnlyTable(silkTouchDrop));
    }

    public void registerDropsOther(Block block, Block silkTouchDrop) {
        registerLootTable(block, createSingleItemTable(silkTouchDrop));
    }

    public void registerDropsWhenSilkTouch(Block block) {
        registerDropsOtherWhenSilkTouch(block, block);
    }

    public void registerDropsSelf(Block blockIn) {
        registerDropsOther(blockIn, blockIn);
    }

    protected void registerLootTable(Block blockIn, Function<Block, LootTable.Builder> factory) {
        this.registerLootTable(blockIn, factory.apply(blockIn));
    }

    protected void registerLootTable(Block blockIn, LootTable.Builder table) {
        registerLootTable(blockIn.getLootTable(), table);
    }
}
