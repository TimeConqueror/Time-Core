package ru.timeconqueror.timecore.molang;

import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.molangcompiler.api.object.MolangLibrary;
import ru.timeconqueror.timecore.animation.watcher.AbstractAnimationTicker;
import ru.timeconqueror.timecore.api.molang.Molang;

import java.util.Map;

public class MolangObjects {
    public static MolangLibrary queriesForTicker(AbstractAnimationTicker ticker) {
        //FIXME
        var queries = Map.of(Molang.Query.Animation.ANIM_TIME, MolangExpression.of(() -> ticker.getAnimationTimeAt(0) / 1000F));
//        var queries = Map.of(Molang.Query.Animation.ANIM_TIME, MolangExpression.of(() -> ticker.getAnimationTimeAt() / 1000F));
//
        return new MolangLibraryImpl("Animation-relative queries", queries);
    }
}
