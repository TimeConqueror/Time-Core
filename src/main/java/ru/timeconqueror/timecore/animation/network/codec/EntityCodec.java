package ru.timeconqueror.timecore.animation.network.codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class EntityCodec extends LevelObjectCodec<Entity> {
    private final int id;

    public EntityCodec(Factory<? extends Entity> parent, Entity entity) {
        super(parent);
        this.id = entity.getId();
    }

    public EntityCodec(Factory<? extends Entity> parent, FriendlyByteBuf buffer) {
        super(parent);
        this.id = buffer.readInt();
    }

    @Override
    protected void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(id);
    }

    @Override
    public Entity construct(Level level) {
        return level.getEntity(id);
    }
}