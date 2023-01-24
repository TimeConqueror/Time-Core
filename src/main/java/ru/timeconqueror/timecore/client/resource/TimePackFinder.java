package ru.timeconqueror.timecore.client.resource;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.flag.FeatureFlagSet;

import java.util.function.Consumer;

public class TimePackFinder implements RepositorySource {
    @Override
    public void loadPacks(Consumer<Pack> onLoad) {
        onLoad.accept(Pack.create("timecore_special_resources",
                Component.literal("TimeCore Special Resources"),
                true,
                TimePackResources::new,
                new Pack.Info(Component.literal("Special resources, used in TimeCore-dependent mods for auto-generating, etc."),
                        10/*DetectedVersion#dataPackVersion*/,
                        12/*DetectedVersion#resourcePackVersion*/,
                        FeatureFlagSet.of(),
                        false),
                PackType.CLIENT_RESOURCES,
                Pack.Position.TOP,
                false,
                PackSource.BUILT_IN
        ));
    }
}
