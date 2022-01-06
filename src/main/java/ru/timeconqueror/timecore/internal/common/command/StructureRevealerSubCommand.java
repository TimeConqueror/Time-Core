package ru.timeconqueror.timecore.internal.common.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ColorArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.common.command.argument.StructureArgument;
import ru.timeconqueror.timecore.internal.devtools.StructureRevealer;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class StructureRevealerSubCommand {
    public static final SimpleCommandExceptionType REVEALER_DEACTIVATED = new SimpleCommandExceptionType(new TranslatableComponent("cmd.timecore.structure_revealer.deactivated"));

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        /*
         * /timecore structure_revealer subscribe *structure_name* *player*|me
         * /timecore structure_revealer unsubscribe *structure_name* *player*|me
         * /timecore structure_revealer unsubscribe_all *player*|me
         * /timecore structure_revealer get_subscriptions *player*|me
         */
        return Commands.literal("structure_revealer")
                .then(Commands.literal("subscribe")
                        .then(Commands.argument("structure", StructureArgument.create())
                                .then(Commands.argument("player", EntityArgument.players())
                                        .executes(context -> subscribe(context.getSource(), EntityArgument.getPlayers(context, "player"), context.getArgument("structure", StructureFeature.class)))
                                ).executes(context -> subscribe(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException()), context.getArgument("structure", StructureFeature.class)))
                        )
                ).then(Commands.literal("unsubscribe")
                        .then(Commands.argument("structure", StructureArgument.create())
                                .then(Commands.argument("player", EntityArgument.players())
                                        .executes(context -> unsubscribe(context.getSource(), EntityArgument.getPlayers(context, "player"), context.getArgument("structure", StructureFeature.class)))
                                ).executes(context -> unsubscribe(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException()), context.getArgument("structure", StructureFeature.class)))
                        )
                ).then(Commands.literal("unsubscribe_from_all")
                        .then(Commands.argument("player", EntityArgument.players())
                                .executes(context -> unsubscribe(context.getSource(), EntityArgument.getPlayers(context, "player"), null))
                        ).executes(context -> unsubscribe(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException()), null))
                ).then(Commands.literal("get_subscriptions")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> getSubscriptions(EntityArgument.getPlayer(context, "player"), context.getSource()))
                        ).executes(context -> getSubscriptions(context.getSource().getPlayerOrException(), context.getSource()))
                );
    }

    private static int subscribe(CommandSourceStack source, Collection<ServerPlayer> players, StructureFeature<?> structure) throws CommandSyntaxException {
        doForRevealer(revealer -> {
            for (ServerPlayer player : players) {
                revealer.subscribePlayerToStructure(player, structure);
            }

            source.sendSuccess(new TranslatableComponent("cmd.timecore.structure_revealer.subscribe.success", playerNamesToString(players), structure.getRegistryName()), true);

        });

        return 1;
    }

    private static int unsubscribe(CommandSourceStack source, Collection<ServerPlayer> players, @Nullable StructureFeature<?> structure) throws CommandSyntaxException {
        doForRevealer(revealer -> {
            String names = playerNamesToString(players);

            if (structure == null) {
                for (ServerPlayer player : players) {
                    revealer.unsubscribePlayerFromAllStructures(player);
                }

                source.sendSuccess(new TranslatableComponent("cmd.timecore.structure_revealer.unsubscribe_from_all.success", names), true);
            } else {
                for (ServerPlayer player : players) {
                    revealer.unsubscribePlayerFromStructure(player, structure);
                }

                source.sendSuccess(new TranslatableComponent("cmd.timecore.structure_revealer.unsubscribe.success", names, structure.getRegistryName()), true);
            }
        });

        return 1;
    }

    private static String playerNamesToString(Collection<ServerPlayer> players) {
        return players.stream().map(player -> player.getName().getString()).collect(Collectors.joining());
    }

    private static int getSubscriptions(ServerPlayer player, CommandSourceStack source) throws CommandSyntaxException {
        doForRevealer(revealer -> {
            List<ResourceLocation> subscriptions = revealer.getSubscriptions(player);

            String listOut = subscriptions.stream()
                    .map(ResourceLocation::toString)
                    .collect(Collectors.joining(","));

            source.sendSuccess(new TranslatableComponent("cmd.timecore.structure_revealer.list", player.getName()).withStyle(ChatFormatting.AQUA)
                    .append(new TextComponent("\n"))
                    .append(new TextComponent(listOut)), false);
        });

        return 1;
    }

    private static void doForRevealer(Consumer<StructureRevealer> action) throws CommandSyntaxException {
        Optional<StructureRevealer> instance = StructureRevealer.getInstance();

        instance.ifPresent(action);
        instance.orElseThrow(REVEALER_DEACTIVATED::create);
    }

    public static class ClientSubCommand {
        public static ArgumentBuilder<CommandSourceStack, ?> register() {
            /*
             * /timecore structure_revealer set_color *structure_name* *color(int)*
             * /timecore structure_revealer set_visible_through_blocks *true|false*
             */
            return Commands.literal("structure_revealer")
                    .then(Commands.literal("set_color")
                            .then(Commands.argument("structure", StructureArgument.create())
                                    .then(Commands.argument("color", ColorArgument.color())
                                            .executes(context -> setStructureColor(context.getArgument("structure", StructureFeature.class), context.getArgument("color", ChatFormatting.class)))
                                    )
                            )
                    ).then(Commands.literal("set_visible_through_blocks")
                            .then(Commands.argument("boolean", BoolArgumentType.bool())
                                    .executes(context -> setVisibleThroughBlocks(context.getArgument("boolean", Boolean.class)))
                            )
                    );
        }

        public static int setStructureColor(StructureFeature<?> structure, ChatFormatting color) throws CommandSyntaxException {
            doForRevealer(revealer -> revealer.structureRenderer.setStructureColor(structure.getRegistryName(), color.getColor()));
            return -1;
        }

        public static int setVisibleThroughBlocks(boolean isVisibleThroughBlocks) throws CommandSyntaxException {
            doForRevealer(revealer -> revealer.structureRenderer.setVisibleThroughBlocks(isVisibleThroughBlocks));

            return -1;
        }
    }
}
