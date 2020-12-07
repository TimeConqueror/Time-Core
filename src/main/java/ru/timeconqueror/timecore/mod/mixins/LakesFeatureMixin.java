package ru.timeconqueror.timecore.mod.mixins;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.BlockStateFeatureConfig;
import net.minecraft.world.gen.feature.LakesFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.timeconqueror.timecore.storage.StructureTags;
import ru.timeconqueror.timecore.storage.StructureTags.Tag;

import java.util.Collection;
import java.util.Random;

@Mixin(LakesFeature.class)
public class LakesFeatureMixin {
    @Inject(
            method = "place",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/math/BlockPos;below(I)Lnet/minecraft/util/math/BlockPos;"),
            cancellable = true
    )
    private void disableLakeBreakingStructures(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, BlockStateFeatureConfig config, CallbackInfoReturnable<Boolean> cir) {
        Collection<Structure<?>> structures = StructureTags.get(Tag.DISABLE_BREAKING_BY_LAKES);

        for (Structure<?> structure : structures) {
            if (reader.startsForFeature(SectionPos.of(pos), structure).findAny().isPresent()) {
                cir.setReturnValue(false);
                break;
            }
        }
    }
}