package ru.timeconqueror.timecore.internal.common.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

//TODO DeferredRegister for commands
@EventBusSubscriber
public class CommandRegistrar {
    @SubscribeEvent
    public static void onSetup(FMLCommonSetupEvent event) {
        //FIXME port
        //   ArgumentTypes.register("timecore.structure", StructureArgument.class, new EmptyArgumentSerializer<>(StructureArgument::new));
    }

    @SubscribeEvent//TODO move to client commands event
    public static void onClient(FMLClientSetupEvent event) {
        //ToDo port?
//        CommandDispatcher<CommandSourceStack> commandDispatcher = ClientCommandDispatcher.get();
//        TimeCoreCommand.registerClient(commandDispatcher);
    }

    @SubscribeEvent
    public static void onServerStart(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> commandDispatcher = event.getDispatcher();
        TimeCoreCommand.register(commandDispatcher);
    }
}
