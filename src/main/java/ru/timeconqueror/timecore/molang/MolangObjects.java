package ru.timeconqueror.timecore.molang;

import gg.moonflower.molangcompiler.api.object.MolangLibrary;
import ru.timeconqueror.timecore.animation.watcher.AbstractAnimationTicker;
import ru.timeconqueror.timecore.api.molang.Molang;
import ru.timeconqueror.timecore.api.molang.TCMolangExpressions;

import java.util.Map;

public class MolangObjects {
    public static MolangLibrary queriesForTicker(AbstractAnimationTicker ticker) {
        var queries = Map.of(Molang.Query.Animation.ANIM_TIME, TCMolangExpressions.usingRuntimeProperties((env, props) -> ticker.getAnimationTimeAt(props.getClockTime()) / 1000F));

        return new MolangLibraryImpl("Animation-relative queries", queries);
    }
}
