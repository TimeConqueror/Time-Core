//package ru.timeconqueror.timecore.mixins;//FIXME remove
//
//import net.minecraft.resources.ResourcePackInfo;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//import ru.timeconqueror.timecore.client.ICustomPrioritized;
//
//import java.util.List;
//import java.util.function.Function;
//
//@Mixin(ResourcePackInfo.Priority.class)
//public class MixinResourcePackInfo {
////    @Inject(
////            method = "<init>",
////            at = @At(
////                    value = "INVOKE",
////                    target = "Lnet/minecraft/resources/ResourcePackList;addPackFinder(Lnet/minecraft/resources/IPackFinder;)V"
////            )
////    )
////    public void initHook(GameConfiguration gameConfig, CallbackInfo ci) {
////        System.out.println("ha ha benis");
////    }
//
//    @Inject(method = "func_198993_a",
//            at = @At(value = "HEAD"),
//            cancellable = true
//    )
//    public <T, P extends ResourcePackInfo> void includePrioritizedPackHook(List<T> resourcePackInfoList, T resourcePackInfoIn, Function<T, P> p_198993_3_, boolean p_198993_4_, CallbackInfoReturnable<Integer> cir) {
//        if (resourcePackInfoIn instanceof ICustomPrioritized) {
//            String afterModId = ((ICustomPrioritized) resourcePackInfoIn).loadAfter();
//
//            int afterIndex = -1;
//            for (int i = 0; i < resourcePackInfoList.size(); i++) {
//                T t = resourcePackInfoList.get(i);
//                ResourcePackInfo resourcePackInfo = ((ResourcePackInfo) t);
//                if (resourcePackInfo.getName().equals(afterModId)) {
//                    afterIndex = i;
//                    break;
//                }
//            }
//
//            if (afterIndex != -1) {
//                resourcePackInfoList.add(afterIndex + 1, resourcePackInfoIn);
//
//                cir.setReturnValue(afterIndex + 1);
//            }
//        }
//    }
//
////    @Inject(method = "func_198993_a",
////            at = @At(value = "JUMP", )
////    )
////    public <T, P extends ResourcePackInfo> void addPackHook(List<T> resourcePackInfoList, T resourcePackInfoIn, Function<T, P> p_198993_3_, boolean p_198993_4_, CallbackInfoReturnable<Integer> cir) {
////        if (resourcePackInfoIn instanceof ICustomPrioritized) {
////            String afterModId = ((ICustomPrioritized) resourcePackInfoIn).loadAfter();
////
////            int afterIndex = -1;
////            for (int i = 0; i < resourcePackInfoList.size(); i++) {
////                T t = resourcePackInfoList.get(i);
////                ResourcePackInfo resourcePackInfo = ((ResourcePackInfo) t);
////                if (resourcePackInfo.getName().equals(afterModId)) {
////                    afterIndex = i;
////                    break;
////                }
////            }
////
////            if (afterIndex != -1) {
////                resourcePackInfoList.add(afterIndex + 1, resourcePackInfoIn);
////
////                cir.setReturnValue(afterIndex + 1);
////            }
////        }
////    }
//}