package ru.timeconqueror.timecore.mod.mixins.accessor;

import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(Structure.class)
public interface StructureAccessor {
    @Intrinsic(displace = true)
    @Invoker("getStarts")
    List<StructureStart> getStarts(IWorld worldIn, int x, int z);
}
