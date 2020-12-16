package ru.timeconqueror.timecore.api.common.config;

import net.minecraftforge.fml.config.ModConfig;

public interface ILoadListener {
    void onEveryLoad(ModConfig.ModConfigEvent event);
}
