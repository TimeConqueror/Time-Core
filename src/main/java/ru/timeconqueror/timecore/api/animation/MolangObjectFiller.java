package ru.timeconqueror.timecore.api.animation;

import gg.moonflower.molangcompiler.api.object.MolangObject;
import ru.timeconqueror.timecore.molang.MolangSharedObjects;
import ru.timeconqueror.timecore.molang.MolangSharedQuery;

public class MolangObjectFiller {
    private final MolangSharedObjects sharedObjects;

    public MolangObjectFiller(MolangSharedObjects sharedObjects) {
        this.sharedObjects = sharedObjects;
    }

    public void add(String name, MolangObject object) {
        sharedObjects.put(name, object);
    }

    public void add(MolangSharedQuery sharedQuery) {
        sharedObjects.put(sharedQuery.getName(), sharedQuery);
    }
}
