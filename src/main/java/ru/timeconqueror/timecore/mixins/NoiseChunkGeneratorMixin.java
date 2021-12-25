package ru.timeconqueror.timecore.mixins;

import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(NoiseBasedChunkGenerator.class)
//FIXME PORT (check Beardifier)
public class NoiseChunkGeneratorMixin {
//    @Redirect(method = "lambda$fillFromNoise$6",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/feature/structure/StructurePiece;isCloseToChunk(Lnet/minecraft/util/math/ChunkPos;I)Z")
//    )
//    private static boolean disableNoiseForSomePieces(StructurePiece piece, ChunkPos pos, int int_) {
//        return !(piece instanceof INoNoiseStructurePiece) && piece.isCloseToChunk(pos, int_);
//    }
}
