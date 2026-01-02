package ru.timeconqueror.timecore.client.resource;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.*;
import net.minecraft.world.flag.FeatureFlagSet;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.internal.common.config.MainConfig;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;

public class TimePackFinder implements RepositorySource {
    @Override
    public void loadPacks(Consumer<Pack> onLoad) {
        PackLocationInfo packLocationInfo = new PackLocationInfo("timecore:special_sources",
                Component.literal("TimeCore Special Resources"),
                PackSource.BUILT_IN,
                Optional.of(new KnownPack(TimeCore.MODID, "special_sources", "1")));
        Pack.Metadata metadata = new Pack.Metadata(Component.literal("Special on-fly resources, used in TimeCore-dependent mods for auto-generating, etc. Can be hidden via config"),
                PackCompatibility.COMPATIBLE,
                FeatureFlagSet.of(),
                Collections.emptyList(),
                MainConfig.INSTANCE.hideTimeCoreResourcePack());

        onLoad.accept(new Pack(
                packLocationInfo,
                BuiltInPackSource.fromName(TimePackResources::new),
                metadata,
                new PackSelectionConfig(true /*so it cant be disabled*/, Pack.Position.TOP, false /*players can choose the resourcepack position*/)
        ));
    }
}
