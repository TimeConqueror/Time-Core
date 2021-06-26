package ru.timeconqueror.timecore.mixins;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.timeconqueror.timecore.api.common.world.structure.INoNoiseStructurePiece;

@Mixin(NoiseChunkGenerator.class)
public class NoiseChunkGeneratorMixin {
    @Redirect(method = "lambda$fillFromNoise$6",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/feature/structure/StructurePiece;isCloseToChunk(Lnet/minecraft/util/math/ChunkPos;I)Z"),
            remap = false
    )
    private static boolean disableNoiseForSomePieces(StructurePiece piece, ChunkPos pos, int int_) {
        return !(piece instanceof INoNoiseStructurePiece) && piece.isCloseToChunk(pos, int_);
    }
}
