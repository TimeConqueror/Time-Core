package examples.animation_example.block_example.block.tile;

import examples.animation_example.block_example.registry.ATileRegistry;
import examples.animation_example.block_example.registry.TileAnimations;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.animation.AnimationSystem;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.animation.AnimationConstants;
import ru.timeconqueror.timecore.api.animation.AnimationStarter;
import ru.timeconqueror.timecore.api.animation.AnimationSystems;

public class TileHeatCube extends BlockEntity implements AnimatedObject<TileHeatCube> {
    private AnimationSystem<TileHeatCube> animationSystem;

    public TileHeatCube(BlockPos worldPosition_, BlockState blockState_) {
        super(ATileRegistry.HEAT_CUBE, worldPosition_, blockState_);
    }

    @Override
    public void onLoad() {
        animationSystem = AnimationSystems.forBlockEntity(this, animationManagerBuilder -> {
        });
    }

    @Override
    public @NotNull AnimationSystem<TileHeatCube> getSystem() {
        return animationSystem;
    }

    public void clientTick(Level level_, BlockPos pos_, BlockState state_) {
        getAnimationSystemApi().startAnimation(AnimationStarter.of(TileAnimations.heatCubeIdle).ignorable(true), AnimationConstants.MAIN_LAYER_NAME);
    }
}
