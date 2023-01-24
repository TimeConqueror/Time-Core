package ru.timeconqueror.timecore.internal.common.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import ru.timeconqueror.timecore.api.common.command.ClientCommandDispatcher;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)//TODO DeferredRegister for commands
public class CommandRegistrar {
    @SubscribeEvent
    public static void onSetup(FMLCommonSetupEvent event) {
        //FIXME port
        //   ArgumentTypes.register("timecore.structure", StructureArgument.class, new EmptyArgumentSerializer<>(StructureArgument::new));
    }

    @SubscribeEvent//TODO move to client commands event
    public static void onClient(FMLClientSetupEvent event) {
        CommandDispatcher<CommandSourceStack> commandDispatcher = ClientCommandDispatcher.get();
        TimeCoreCommand.registerClient(commandDispatcher);
    }

    @Mod.EventBusSubscriber
    public static class ForgeBusCommandRegistrar {
        @SubscribeEvent
        public static void onServerStart(RegisterCommandsEvent event) {
            CommandDispatcher<CommandSourceStack> commandDispatcher = event.getDispatcher();
            TimeCoreCommand.register(commandDispatcher);
        }
    }
}
