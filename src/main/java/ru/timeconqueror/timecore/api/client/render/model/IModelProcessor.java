package ru.timeconqueror.timecore.api.client.render.model;

import ru.timeconqueror.timecore.client.render.processor.LookAtViewDirectionProcessor;

/**
 * Allows to manipulate model transformation before it will be rendered.
 *
 * @see LookAtViewDirectionProcessor
 */
public interface IModelProcessor<T> {
    /**
     * Method, which can be used to apply some manually handled transformation.
     * Be attentive, if you use {@link IModelProcessor} in couple with animated stuff renderers,
     * then the animation transformation is already applied on parts at this point.
     * Because of this fact, it's advisable to use add/mul operations to transform parts instead of overwriting values.
     */
    void process(T object, ITimeModel model, float partialTick);
}
