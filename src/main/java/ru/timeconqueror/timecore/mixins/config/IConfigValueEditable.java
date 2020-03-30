package ru.timeconqueror.timecore.mixins.config;

//TODO move to TimeCore
public interface IConfigValueEditable {
    void addLineToComment(String addition);

    void setLangKey(String key);
}
