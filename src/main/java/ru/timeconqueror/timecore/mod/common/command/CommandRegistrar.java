package ru.timeconqueror.timecore.mod.common.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import ru.timeconqueror.timecore.api.common.command.ClientCommandDispatcher;
import ru.timeconqueror.timecore.api.common.command.argument.StructureArgument;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)//TODO DeferredRegister for commands
public class CommandRegistrar {
    @SubscribeEvent
    public static void onSetup(FMLCommonSetupEvent event) {
        ArgumentTypes.register("timecore.structure", StructureArgument.class, new ArgumentSerializer<>(StructureArgument::new));
    }

    @SubscribeEvent//TODO move to client commands event
    public static void onClient(FMLClientSetupEvent event) {
        CommandDispatcher<CommandSource> commandDispatcher = ClientCommandDispatcher.get();
        TimeCoreCommand.registerClient(commandDispatcher);
    }

    @Mod.EventBusSubscriber
    public static class ForgeBusCommandRegistrar {
        @SubscribeEvent
        public static void onServerStart(RegisterCommandsEvent event) {
            CommandDispatcher<CommandSource> commandDispatcher = event.getDispatcher();
            TimeCoreCommand.register(commandDispatcher);
        }
    }
}
