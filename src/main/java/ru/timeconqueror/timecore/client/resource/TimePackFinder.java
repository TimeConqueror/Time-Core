package ru.timeconqueror.timecore.client.resource;

import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.PackCompatibility;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.util.text.StringTextComponent;

import java.util.function.Consumer;

public class TimePackFinder implements IPackFinder {
    @Override
    public void loadPacks(Consumer<ResourcePackInfo> mapInserter, ResourcePackInfo.IFactory iFactory) {
        TimeSpecialResourcePack tSpecialPack = new TimeSpecialResourcePack();
        ResourcePackInfo tSpecialPackInfo = new ResourcePackInfo("timecore_special_resources",
                true,
                () -> tSpecialPack,
                new StringTextComponent(tSpecialPack.getName()),
                new StringTextComponent("Special resources, used in TimeCore-dependent mods for auto-generating, etc."),
                PackCompatibility.COMPATIBLE,
                ResourcePackInfo.Priority.TOP,
                false,
                IPackNameDecorator.BUILT_IN,
                false
        );

        mapInserter.accept(tSpecialPackInfo);
    }
}
