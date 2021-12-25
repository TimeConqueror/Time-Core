package ru.timeconqueror.timecore.mixins;

import net.minecraft.world.level.levelgen.feature.LakeFeature;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LakeFeature.class)
//FIXME PORT
public class LakesFeatureMixin {
//    @Inject(
//            method = "place",
//            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/math/BlockPos;below(I)Lnet/minecraft/util/math/BlockPos;"),
//            cancellable = true
//    )
//    private void disableLakeBreakingStructures(WorldGenLevel reader, ChunkGenerator generator, Random rand, BlockPos pos, BlockStateConfiguration config, CallbackInfoReturnable<Boolean> cir) {
//        Collection<StructureFeature<?>> structures = StructureTags.get(Tag.DISABLE_BREAKING_BY_LAKES);
//
//        for (StructureFeature<?> structure : structures) {
//            if (reader.startsForFeature(SectionPos.of(pos), structure).findAny().isPresent()) {
//                cir.setReturnValue(false);
//                break;
//            }
//        }
//    }
}