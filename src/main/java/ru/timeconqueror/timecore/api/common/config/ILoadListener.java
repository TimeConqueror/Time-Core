package ru.timeconqueror.timecore.api.common.config;


import net.neoforged.fml.event.config.ModConfigEvent;

public interface ILoadListener {
    void onEveryLoad(ModConfigEvent event);
}
