//package ru.timeconqueror.timecore.common.capability.owner.attach;
//
//import net.minecraft.core.Direction;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.nbt.Tag;
//import net.minecraftforge.common.capabilities.Capability;
//import net.minecraftforge.common.capabilities.ICapabilityProvider;
//import net.minecraftforge.common.util.INBTSerializable;
//import net.minecraftforge.common.util.LazyOptional;
//import org.jetbrains.annotations.NotNull;
//import ru.timeconqueror.timecore.common.capability.owner.attach.getter.CapabilityProviderAdapter;
//
//import javax.annotation.Nullable;
//import java.util.HashMap;
//import java.util.Map;
//
//public class CoffeeCapabilityProvider<T> implements ICapabilityProvider, INBTSerializable<CompoundTag> {
//
//    private final T target;
//    private final HashMap<String, CapabilityProviderAdapter<T, ?>> providers = new HashMap<>();
//
//    public CoffeeCapabilityProvider(T target) {
//        this.target = target;
//    }
//
//    public <C> void addCapability(Capability<C> capability, CapabilityProviderAdapter<T, C> provider) {
//        providers.put(capability.getName(), provider);
//        // for init
//        provider.getCapability(target, null);
//    }
//
//    @Override
//    @NotNull
//    public <C> LazyOptional<C> getCapability(@NotNull Capability<C> capability, @Nullable Direction side) {
//        CapabilityProviderAdapter<T, ?> provider = providers.get(capability.getName());
//
//        if (provider != null) {
//            C cap = (C) provider.getCapability(target, side);
//
//            if (cap != null) {
//                return LazyOptional.of(() -> cap);
//            }
//        }
//
//        return LazyOptional.empty();
//    }
//
//    @Override
//    public CompoundTag serializeNBT() {
//        CompoundTag root = new CompoundTag();
//
//        for (Map.Entry<String, CapabilityProviderAdapter<T, ?>> entry : providers.entrySet()) {
//            Object cap = entry.getValue().getCapability(target, null);
//
//            if (cap instanceof INBTSerializable) {
//                root.put(entry.getKey(), ((INBTSerializable<Tag>) cap).serializeNBT());
//            }
//        }
//
//        return root;
//    }
//
//    @Override
//    public void deserializeNBT(CompoundTag nbt) {
//        for (Map.Entry<String, CapabilityProviderAdapter<T, ?>> entry : providers.entrySet()) {
//            Object cap = entry.getValue().getCapability(target, null);
//
//            if (cap instanceof INBTSerializable && nbt.contains(entry.getKey())) {
//                CompoundTag comp = nbt.getCompound(entry.getKey());
//                ((INBTSerializable<Tag>) cap).deserializeNBT(comp);
//            }
//        }
//    }
//}
