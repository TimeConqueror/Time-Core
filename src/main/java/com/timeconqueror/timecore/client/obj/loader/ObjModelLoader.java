package com.timeconqueror.timecore.client.obj.loader;

import net.minecraft.util.ResourceLocation;

public class ObjModelLoader {

    public static ObjModelRaw load(ResourceLocation resourceLocation){
        return new ObjModelBuilder(resourceLocation).loadModel();
    }
}
