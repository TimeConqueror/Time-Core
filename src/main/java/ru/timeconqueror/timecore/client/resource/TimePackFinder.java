package ru.timeconqueror.timecore.client.resource;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;

import java.util.function.Consumer;

public class TimePackFinder implements RepositorySource {
    @Override
    public void loadPacks(Consumer<Pack> mapInserter, Pack.PackConstructor iFactory) {
        TimeSpecialResourcePack tSpecialPack = new TimeSpecialResourcePack();
        Pack tSpecialPackInfo = new Pack("timecore_special_resources",
                true,
                () -> tSpecialPack,
                new TextComponent(tSpecialPack.getName()),
                new TextComponent("Special resources, used in TimeCore-dependent mods for auto-generating, etc."),
                PackCompatibility.COMPATIBLE,
                Pack.Position.TOP,
                false,
                PackSource.BUILT_IN,
                false
        );

        mapInserter.accept(tSpecialPackInfo);
    }
}
