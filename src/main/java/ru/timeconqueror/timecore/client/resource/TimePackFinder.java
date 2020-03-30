package ru.timeconqueror.timecore.client.resource;

import net.minecraft.client.resources.ClientResourcePackInfo;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.PackCompatibility;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.util.text.StringTextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class TimePackFinder implements IPackFinder {

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ResourcePackInfo> void addPackInfosToMap(@NotNull Map<String, T> nameToPackMap, @NotNull ResourcePackInfo.IFactory<T> packInfoFactory) {
        TimeSpecialResourcePack tSpecialPack = new TimeSpecialResourcePack();
        ClientResourcePackInfo tSpecialPackInfo = new ClientResourcePackInfo("timecore_special_resources",
                true,
                () -> tSpecialPack,
                new StringTextComponent(tSpecialPack.getName()),
                new StringTextComponent("Special resources, used in TimeCore-dependent mods for auto-generating, etc."),
                PackCompatibility.COMPATIBLE,
                ResourcePackInfo.Priority.TOP,
                false,
                null,
                false
        );

        nameToPackMap.put(tSpecialPackInfo.getName(), ((T) tSpecialPackInfo));
    }
}
