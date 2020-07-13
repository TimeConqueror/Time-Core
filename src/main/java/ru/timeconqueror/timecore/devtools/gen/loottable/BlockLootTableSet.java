package ru.timeconqueror.timecore.devtools.gen.loottable;

import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.*;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.IProperty;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.*;
import net.minecraft.world.storage.loot.functions.*;

import java.util.function.Function;

public abstract class BlockLootTableSet extends LootTableSet {
    protected static final ILootCondition.IBuilder SILK_TOUCH = MatchTool.builder(ItemPredicate.Builder.create().enchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.IntBound.atLeast(1))));
    protected static final ILootCondition.IBuilder NO_SILK_TOUCH = SILK_TOUCH.inverted();
    protected static final ILootCondition.IBuilder SHEARS = MatchTool.builder(ItemPredicate.Builder.create().item(Items.SHEARS));
    protected static final ILootCondition.IBuilder SILK_TOUCH_OR_SHEARS = SHEARS.alternative(SILK_TOUCH);
    protected static final ILootCondition.IBuilder NOT_SILK_TOUCH_OR_SHEARS = SILK_TOUCH_OR_SHEARS.inverted();

    /**
     * Removes some items from a stack, if there was an explosion. Each item has a chance of 1/explosion radius to be lost.
     */
    protected static <T> T withExplosionDecay(ILootFunctionConsumer<T> lootFunctionConsumer) {
        return lootFunctionConsumer.acceptFunction(ExplosionDecay.builder());
    }

    /**
     * Simply passes if the item would survive an explosion.
     * If there was no explosion, this condition always passes.
     * If there was an explosion, the chance of passing depends on the distance to the explosion and the explosion radius.
     */
    protected static <T> T withSurvivesExplosion(ILootConditionConsumer<T> conditionConsumer) {
        return conditionConsumer.acceptCondition(SurvivesExplosion.builder());
    }

    protected static LootTable.Builder dropping(IItemProvider drop) {
        return LootTable.builder().addLootPool(withSurvivesExplosion(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(drop))));
    }

    protected static LootTable.Builder dropping(ILootCondition.IBuilder condition, Block defaultDrop, LootEntry.Builder<?> alternative) {
        return LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(defaultDrop).acceptCondition(condition).alternatively(alternative)));
    }

    protected static LootTable.Builder droppingWithSilkTouch(Block silkTouchDrop, LootEntry.Builder<?> alternative) {
        return dropping(SILK_TOUCH, silkTouchDrop, alternative);
    }

    protected static LootTable.Builder droppingWithShears(Block defaultDrop, LootEntry.Builder<?> alternative) {
        return dropping(SHEARS, defaultDrop, alternative);
    }

    protected static LootTable.Builder droppingWithSilkTouchOrShears(Block defaultDrop, LootEntry.Builder<?> alternative) {
        return dropping(SILK_TOUCH_OR_SHEARS, defaultDrop, alternative);
    }

    protected static LootTable.Builder droppingWithSilkTouch(Block silkTouchDrop, IItemProvider alternative) {
        return droppingWithSilkTouch(silkTouchDrop, withSurvivesExplosion(ItemLootEntry.builder(alternative)));
    }

    protected static LootTable.Builder droppingRandomly(IItemProvider drop, IRandomRange range) {
        return LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(withExplosionDecay(ItemLootEntry.builder(drop).acceptFunction(SetCount.builder(range)))));
    }

    protected static LootTable.Builder droppingWithSilkTouchOrRandomly(Block silkTouchDrop, IItemProvider alternative, IRandomRange alternativeRange) {
        return droppingWithSilkTouch(silkTouchDrop, withExplosionDecay(ItemLootEntry.builder(alternative).acceptFunction(SetCount.builder(alternativeRange))));
    }

    protected static LootTable.Builder onlyWithSilkTouch(IItemProvider silkTouchDrop) {
        return LootTable.builder().addLootPool(LootPool.builder().acceptCondition(SILK_TOUCH).rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(silkTouchDrop)));
    }

    protected static LootTable.Builder droppingAndFlowerPot(IItemProvider drop) {
        return LootTable.builder().addLootPool(withSurvivesExplosion(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Blocks.FLOWER_POT)))).addLootPool(withSurvivesExplosion(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(drop))));
    }

    protected static LootTable.Builder droppingSlab(Block dropSlab) {
        return LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(withExplosionDecay(ItemLootEntry.builder(dropSlab).acceptFunction(SetCount.builder(ConstantRange.of(2)).acceptCondition(BlockStateProperty.builder(dropSlab).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withProp(SlabBlock.TYPE, SlabType.DOUBLE)))))));
    }

    protected static <T extends Comparable<T> & IStringSerializable> LootTable.Builder droppingWhen(Block block, IProperty<T> property, T propertyVal) {
        return LootTable.builder().addLootPool(withSurvivesExplosion(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(block).acceptCondition(BlockStateProperty.builder(block).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withProp(property, propertyVal))))));
    }

    protected static LootTable.Builder droppingWithName(Block dropBlock) {
        return LootTable.builder().addLootPool(withSurvivesExplosion(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(dropBlock).acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY)))));
    }

    protected static LootTable.Builder droppingWithContents(Block dropBlock) {
        return LootTable.builder().addLootPool(withSurvivesExplosion(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(dropBlock).acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY)).acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY).replaceOperation("Lock", "BlockEntityTag.Lock").replaceOperation("LootTable", "BlockEntityTag.LootTable").replaceOperation("LootTableSeed", "BlockEntityTag.LootTableSeed")).acceptFunction(SetContents.builder().addLootEntry(DynamicLootEntry.func_216162_a(ShulkerBoxBlock.CONTENTS))))));
    }

    protected static LootTable.Builder droppingWithPatterns(Block dropBlock) {
        return LootTable.builder().addLootPool(withSurvivesExplosion(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(dropBlock).acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY)).acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY).replaceOperation("Patterns", "BlockEntityTag.Patterns")))));
    }

    protected static LootTable.Builder droppingItemWithFortune(Block silkTouchDrop, Item fortuneDrop) {
        return droppingWithSilkTouch(silkTouchDrop, withExplosionDecay(ItemLootEntry.builder(fortuneDrop).acceptFunction(ApplyBonus.oreDrops(Enchantments.FORTUNE))));
    }

    /**
     * Creates a builder that drops the given IItemProvider in amounts between 0 and 2, most often 0. Only used in
     * vanilla for huge mushroom blocks.
     */
    protected static LootTable.Builder droppingItemRarely(Block silkTouchDrop, IItemProvider defaultDrop) {
        return droppingWithSilkTouch(silkTouchDrop, withExplosionDecay(ItemLootEntry.builder(defaultDrop).acceptFunction(SetCount.builder(RandomValueRange.of(-6.0F, 2.0F))).acceptFunction(LimitCount.func_215911_a(IntClamper.func_215848_a(0)))));
    }

    protected static LootTable.Builder droppingSeeds(Block dropWhenSheared) {
        return droppingWithShears(dropWhenSheared, withExplosionDecay((ItemLootEntry.builder(Items.WHEAT_SEEDS).acceptCondition(RandomChance.builder(0.125F))).acceptFunction(ApplyBonus.uniformBonusCount(Enchantments.FORTUNE, 2))));
    }

    /**
     * Creates a builder that drops the given IItemProvider in amounts between 0 and 3, based on the AGE property. Only
     * used in vanilla for pumpkin and melon stems.
     */
    protected static LootTable.Builder droppingByAge(Block dropFrom, Item drop) {
        return LootTable.builder().addLootPool(withExplosionDecay(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(drop).acceptFunction(SetCount.builder(BinomialRange.of(3, 0.06666667F)).acceptCondition(BlockStateProperty.builder(dropFrom).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(StemBlock.AGE, 0)))).acceptFunction(SetCount.builder(BinomialRange.of(3, 0.13333334F)).acceptCondition(BlockStateProperty.builder(dropFrom).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(StemBlock.AGE, 1)))).acceptFunction(SetCount.builder(BinomialRange.of(3, 0.2F)).acceptCondition(BlockStateProperty.builder(dropFrom).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(StemBlock.AGE, 2)))).acceptFunction(SetCount.builder(BinomialRange.of(3, 0.26666668F)).acceptCondition(BlockStateProperty.builder(dropFrom).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(StemBlock.AGE, 3)))).acceptFunction(SetCount.builder(BinomialRange.of(3, 0.33333334F)).acceptCondition(BlockStateProperty.builder(dropFrom).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(StemBlock.AGE, 4)))).acceptFunction(SetCount.builder(BinomialRange.of(3, 0.4F)).acceptCondition(BlockStateProperty.builder(dropFrom).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(StemBlock.AGE, 5)))).acceptFunction(SetCount.builder(BinomialRange.of(3, 0.46666667F)).acceptCondition(BlockStateProperty.builder(dropFrom).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(StemBlock.AGE, 6)))).acceptFunction(SetCount.builder(BinomialRange.of(3, 0.53333336F)).acceptCondition(BlockStateProperty.builder(dropFrom).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(StemBlock.AGE, 7)))))));
    }

    protected static LootTable.Builder onlyWithShears(IItemProvider dropWithShears) {
        return LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).acceptCondition(SHEARS).addEntry(ItemLootEntry.builder(dropWithShears)));
    }

    /**
     * Used for all leaves, drops self with silk touch, otherwise drops the second Block param with the passed chances
     * for fortune levels, adding in sticks.
     */
    protected static LootTable.Builder droppingWithChancesAndSticks(Block silkTouchOrShearDrop, Block drop, float... dropRates) {
        return droppingWithSilkTouchOrShears(silkTouchOrShearDrop, withSurvivesExplosion(ItemLootEntry.builder(drop)).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, dropRates))).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).acceptCondition(NOT_SILK_TOUCH_OR_SHEARS).addEntry(withExplosionDecay(ItemLootEntry.builder(Items.STICK).acceptFunction(SetCount.builder(RandomValueRange.of(1.0F, 2.0F)))).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, 0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F))));
    }

    /**
     * Used for oak and dark oak, same as droppingWithChancesAndSticks but adding in apples.
     */
    protected static LootTable.Builder droppingWithChancesSticksAndApples(Block silkTouchOrShearDrop, Block drop, float... dropRates) {
        return droppingWithChancesAndSticks(silkTouchOrShearDrop, drop, dropRates).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).acceptCondition(NOT_SILK_TOUCH_OR_SHEARS).addEntry(withSurvivesExplosion(ItemLootEntry.builder(Items.APPLE)).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F))));
    }

    /**
     * Drops the first item parameter always, and the second item parameter plus more of the first when the loot
     * condition is met, applying fortune to only the second argument.
     */
    protected static LootTable.Builder droppingAndBonusWhen(Item alwaysDrop, Item secondDrop, ILootCondition.IBuilder lootCondition) {
        return withExplosionDecay(LootTable.builder().addLootPool(LootPool.builder().addEntry(ItemLootEntry.builder(alwaysDrop).acceptCondition(lootCondition).alternatively(ItemLootEntry.builder(secondDrop)))).addLootPool(LootPool.builder().acceptCondition(lootCondition).addEntry(ItemLootEntry.builder(secondDrop).acceptFunction(ApplyBonus.binomialWithBonusCount(Enchantments.FORTUNE, 0.5714286F, 3)))));
    }

    public static LootTable.Builder builder() {
        return LootTable.builder();
    }

    @Override
    public LootParameterSet getParameterSet() {
        return LootParameterSets.BLOCK;
    }

    public void registerFlowerPot(Block potPlaceable) {
        this.registerLootTable(potPlaceable, (flowerPot) -> droppingAndFlowerPot(((FlowerPotBlock) flowerPot).func_220276_d()));
    }

    public void registerSilkTouchOnly(Block blockIn, Block silkTouchDrop) {
        this.registerLootTable(blockIn, onlyWithSilkTouch(silkTouchDrop));
    }

    public void registerDropping(Block blockIn, IItemProvider drop) {
        this.registerLootTable(blockIn, dropping(drop));
    }

    public void registerSilkTouchOnly(Block blockIn) {
        this.registerSilkTouchOnly(blockIn, blockIn);
    }

    public void registerDropSelfLootTable(Block block) {
        this.registerDropping(block, block);
    }

    protected void registerLootTable(Block blockIn, Function<Block, LootTable.Builder> factory) {
        this.registerLootTable(blockIn, factory.apply(blockIn));
    }

    protected void registerLootTable(Block blockIn, LootTable.Builder table) {
        registerLootTable(blockIn.getLootTable(), table);
    }
}
