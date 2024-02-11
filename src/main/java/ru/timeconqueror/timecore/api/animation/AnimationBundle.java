package ru.timeconqueror.timecore.api.animation;

import lombok.Getter;
import ru.timeconqueror.timecore.api.animation.action.ActionInstance;
import ru.timeconqueror.timecore.api.animation.action.AnimationUpdateListener;
import ru.timeconqueror.timecore.api.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class AnimationBundle<T extends AnimatedObject<T>, DATA> {
    private final List<AnimationUpdateListener<? super T, DATA>> actionList = new ArrayList<>(0);
    private AnimationStarter starter;
    private String layerName;

    public static <T extends AnimatedObject<T>, DATA> AnimationBundleBuilder<T, DATA> builder() {
        return new AnimationBundleBuilder<>();
    }

    public List<ActionInstance<? super T, DATA>> mapActionsToInstances(DATA data) {
        return CollectionUtils.mapList(getActionList(), action -> ActionInstance.<T, DATA>of(action, data));
    }

    public static class AnimationBundleBuilder<T extends AnimatedObject<T>, DATA> {
        private final AnimationBundle<T, DATA> bundle = new AnimationBundle<>();

        public AnimationBundleBuilder<T, DATA> starter(AnimationStarter starter) {
            bundle.starter = starter;
            return this;
        }

        public AnimationBundleBuilder<T, DATA> layerName(String layerName) {
            bundle.layerName = layerName;
            return this;
        }

        public AnimationBundleBuilder<T, DATA> action(AnimationUpdateListener<? super T, DATA> action) {
            bundle.actionList.add(action);
            return this;
        }

        public AnimationBundleBuilder<T, DATA> actions(List<AnimationUpdateListener<? super T, DATA>> actions) {
            bundle.actionList.addAll(actions);
            return this;
        }

        public AnimationBundle<T, DATA> build() {
            Objects.requireNonNull(bundle.starter);
            Objects.requireNonNull(bundle.layerName);

            return bundle;
        }
    }
}
