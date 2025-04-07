package ru.timeconqueror.timecore.client.render.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;

public interface PartLink {
    static PartLink of(String partName) {
        return new PartLinkImpl(partName);
    }

    static PartLink ofRoot() {
        return RootLink.INSTANCE;
    }

    TimeModelPart getPart(ITimeModel model);

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    class RootLink implements PartLink {
        public static final RootLink INSTANCE = new RootLink();

        @Override
        public TimeModelPart getPart(ITimeModel model) {
            return model.getRoot();
        }
    }

    @RequiredArgsConstructor
    class PartLinkImpl implements PartLink {
        private final String partName;

        @Override
        public TimeModelPart getPart(ITimeModel model) {
            return model.getPart(partName);
        }
    }
}
