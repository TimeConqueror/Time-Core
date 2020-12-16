package ru.timeconqueror.timecore.common.world.structure.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.Template;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.init.TStructureProcessorTypes;
import ru.timeconqueror.timecore.util.ExtraCodecs;
import ru.timeconqueror.timecore.util.RandHelper;

public class RandomizeBlockProcessor extends StructureProcessor {
    public static final Codec<RandomizeBlockProcessor> CODEC = RecordCodecBuilder.create(instance ->
            instance
                    .group(ExtraCodecs.BLOCK.fieldOf("to_replace").forGetter(p -> p.toReplace),
                            ExtraCodecs.BLOCK.fieldOf("randomized").forGetter(p -> p.randomized))
                    .apply(instance, RandomizeBlockProcessor::new)
    );

    private final Block toReplace;
    private final Block randomized;

    public RandomizeBlockProcessor(Block toReplace, Block randomized) {
        this.toReplace = toReplace;
        this.randomized = randomized;
    }

    @Nullable
    @Override
    public Template.BlockInfo process(IWorldReader world, BlockPos blockPos_, BlockPos blockPos1_, Template.BlockInfo blockInfo_, Template.BlockInfo blockInfo, PlacementSettings placementSettings_, @Nullable Template template) {
        if (blockInfo.state.getBlock() == toReplace && RandHelper.chance(placementSettings_.getRandom(blockInfo.pos), 10)) {
            return new Template.BlockInfo(blockInfo.pos, randomized.defaultBlockState(), blockInfo.nbt);
        }

        return blockInfo;
    }

    @Override
    protected IStructureProcessorType<?> getType() {
        return TStructureProcessorTypes.RANDOMIZE_BLOCK_PROCESSOR;
    }
}