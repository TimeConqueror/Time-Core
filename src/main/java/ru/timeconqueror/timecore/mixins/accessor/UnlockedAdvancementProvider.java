package ru.timeconqueror.timecore.mixins.accessor;

import net.minecraft.advancements.Advancement;
import net.minecraft.data.advancements.AdvancementProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.nio.file.Path;

@Mixin(AdvancementProvider.class)
public interface UnlockedAdvancementProvider {
//    @Invoker("createPath")
//    static Path createPath(Path pathIn, Advancement advancementIn) {
//        throw new IllegalStateException("Mixin " + UnlockedAdvancementProvider.class + " doesn't work.");
//    } //FIXME port?
}