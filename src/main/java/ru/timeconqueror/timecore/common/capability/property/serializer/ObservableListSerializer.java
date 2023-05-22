package ru.timeconqueror.timecore.common.capability.property.serializer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.common.capability.property.CoffeeObservableList;

import java.util.List;
import java.util.function.Supplier;

public class ObservableListSerializer<T> implements IPropertySerializer<CoffeeObservableList<T>> {
    private final Supplier<List<T>> listCreator;
    private final IPropertySerializer<T> entrySerializer;

    public ObservableListSerializer(Supplier<List<T>> listCreator, IPropertySerializer<T> entrySerializer) {
        this.listCreator = listCreator;
        this.entrySerializer = entrySerializer;
    }

    @Override
    public void serialize(@NotNull String name, CoffeeObservableList<T> list, @NotNull CompoundTag nbt) {
        ListTag tags = new ListTag();
        for (T e : list) {
            CompoundTag tag = new CompoundTag();
            entrySerializer.serialize("value", e, tag);
            tags.add(tag);
        }
        nbt.put(name, tags);
    }

    @Override
    public CoffeeObservableList<T> deserialize(@NotNull String name, @NotNull CompoundTag nbt) {
        ListTag list = nbt.getList(name, Tag.TAG_COMPOUND);
        List<T> outList = listCreator.get();
        for (Tag tag : list) {
            CompoundTag compound = (CompoundTag) tag;
            outList.add(entrySerializer.deserialize("value", compound));
        }
        return CoffeeObservableList.observe(outList);
    }
}
