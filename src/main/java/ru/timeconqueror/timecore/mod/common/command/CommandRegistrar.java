package ru.timeconqueror.timecore.mod.common.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import ru.timeconqueror.timecore.client.command.ClientCommandManager;
import ru.timeconqueror.timecore.common.command.argument.StructureArgument;
import ru.timeconqueror.timecore.registry.TimeAutoRegistrable;

@TimeAutoRegistrable(target = TimeAutoRegistrable.Target.CLASS)
@Mod.EventBusSubscriber//TODO DeferredRegister for commands
public class CommandRegistrar {
    @SubscribeEvent
    public static void onServerStart(FMLServerStartingEvent event) {
        CommandDispatcher<CommandSource> commandDispatcher = event.getCommandDispatcher();
        TimeCoreCommand.register(commandDispatcher);
    }

    @SubscribeEvent
    public static void onSetup(FMLCommonSetupEvent event) {
        ArgumentTypes.register("timecore.structure", StructureArgument.class, new ArgumentSerializer<>(StructureArgument::new));
    }

    @SubscribeEvent//TODO move to client commands event
    public static void onClient(FMLClientSetupEvent event) {
        CommandDispatcher<CommandSource> commandDispatcher = ClientCommandManager.getClientDispatcher();
        TimeCoreCommand.registerClient(commandDispatcher);
    }
}
