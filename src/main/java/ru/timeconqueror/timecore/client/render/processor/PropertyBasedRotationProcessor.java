package ru.timeconqueror.timecore.client.render.processor;

import lombok.RequiredArgsConstructor;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;
import ru.timeconqueror.timecore.api.client.render.model.IModelProcessor;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;
import ru.timeconqueror.timecore.api.util.MathUtils;
import ru.timeconqueror.timecore.client.render.model.PartLink;

@RequiredArgsConstructor
public abstract class PropertyBasedRotationProcessor<T extends BlockEntity> implements IModelProcessor<T> {
    private final PartLink partLink;

    public static <T extends BlockEntity> PropertyBasedRotationProcessor<T> horizontalDirectionalBased(PartLink partLink) {
        return new PropertyBasedRotationProcessor<>(partLink) {
            @Override
            protected void applyRotation(Vector3f targetRotation, T object, BlockState state, ITimeModel model, float partialTick) {
                float yRot = state.getValue(HorizontalDirectionalBlock.FACING).toYRot();
                targetRotation.add(0, -MathUtils.toRadians(yRot), 0);
            }
        };
    }

    @Override
    public void process(T object, ITimeModel model, float partialTick) {
        BlockState state = object.getBlockState();

        Vector3f targetRotation = partLink.getPart(model).getRotation();
        applyRotation(targetRotation, object, state, model, partialTick);
    }

    protected abstract void applyRotation(Vector3f rootRotation, T object, BlockState state, ITimeModel model, float partialTick);
}
