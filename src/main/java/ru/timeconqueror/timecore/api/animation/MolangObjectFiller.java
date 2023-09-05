package ru.timeconqueror.timecore.api.animation;

import gg.moonflower.molangcompiler.api.object.MolangObject;
import ru.timeconqueror.timecore.molang.MolangSharedQuery;
import ru.timeconqueror.timecore.molang.SharedMolangObject;

public class MolangObjectFiller {
    private final SharedMolangObject sharedObjects;

    public MolangObjectFiller(SharedMolangObject sharedObjects) {
        this.sharedObjects = sharedObjects;
    }

    public void add(String name, MolangObject object) {
        sharedObjects.put(name, object);
    }

    public void add(MolangSharedQuery sharedQuery) {
        sharedObjects.put(sharedQuery.getName(), sharedQuery);
    }
}
