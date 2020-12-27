package ru.timeconqueror.timecore.mod.common.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ColorArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.feature.structure.Structure;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.common.command.argument.StructureArgument;
import ru.timeconqueror.timecore.devtools.StructureRevealer;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class StructureRevealerSubCommand {
    public static final SimpleCommandExceptionType REVEALER_DEACTIVATED = new SimpleCommandExceptionType(new TranslationTextComponent("cmd.timecore.structure_revealer.deactivated"));

    public static ArgumentBuilder<CommandSource, ?> register() {
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
                                        .executes(context -> subscribe(context.getSource(), EntityArgument.getPlayers(context, "player"), context.getArgument("structure", Structure.class)))
                                ).executes(context -> subscribe(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException()), context.getArgument("structure", Structure.class)))
                        )
                ).then(Commands.literal("unsubscribe")
                        .then(Commands.argument("structure", StructureArgument.create())
                                .then(Commands.argument("player", EntityArgument.players())
                                        .executes(context -> unsubscribe(context.getSource(), EntityArgument.getPlayers(context, "player"), context.getArgument("structure", Structure.class)))
                                ).executes(context -> unsubscribe(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException()), context.getArgument("structure", Structure.class)))
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

    private static int subscribe(CommandSource source, Collection<ServerPlayerEntity> players, Structure<?> structure) throws CommandSyntaxException {
        doForRevealer(revealer -> {
            for (ServerPlayerEntity player : players) {
                revealer.subscribePlayerToStructure(player, structure);
            }

            source.sendSuccess(new TranslationTextComponent("cmd.timecore.structure_revealer.subscribe.success", playerNamesToString(players), structure.getRegistryName()), true);

        });

        return 1;
    }

    private static int unsubscribe(CommandSource source, Collection<ServerPlayerEntity> players, @Nullable Structure<?> structure) throws CommandSyntaxException {
        doForRevealer(revealer -> {
            String names = playerNamesToString(players);

            if (structure == null) {
                for (ServerPlayerEntity player : players) {
                    revealer.unsubscribePlayerFromAllStructures(player);
                }

                source.sendSuccess(new TranslationTextComponent("cmd.timecore.structure_revealer.unsubscribe_from_all.success", names), true);
            } else {
                for (ServerPlayerEntity player : players) {
                    revealer.unsubscribePlayerFromStructure(player, structure);
                }

                source.sendSuccess(new TranslationTextComponent("cmd.timecore.structure_revealer.unsubscribe.success", names, structure.getRegistryName()), true);
            }
        });

        return 1;
    }

    private static String playerNamesToString(Collection<ServerPlayerEntity> players) {
        return players.stream().map(player -> player.getName().getString()).collect(Collectors.joining());
    }

    private static int getSubscriptions(ServerPlayerEntity player, CommandSource source) throws CommandSyntaxException {
        doForRevealer(revealer -> {
            List<ResourceLocation> subscriptions = revealer.getSubscriptions(player);

            String listOut = subscriptions.stream()
                    .map(ResourceLocation::toString)
                    .collect(Collectors.joining(","));

            source.sendSuccess(new TranslationTextComponent("cmd.timecore.structure_revealer.list", player.getName()).withStyle(TextFormatting.AQUA)
                    .append(new StringTextComponent("\n"))
                    .append(new StringTextComponent(listOut)), false);
        });

        return 1;
    }

    private static void doForRevealer(Consumer<StructureRevealer> action) throws CommandSyntaxException {
        Optional<StructureRevealer> instance = StructureRevealer.getInstance();

        instance.ifPresent(action);
        instance.orElseThrow(REVEALER_DEACTIVATED::create);
    }

    public static class ClientSubCommand {
        public static ArgumentBuilder<CommandSource, ?> register() {
            /*
             * /timecore structure_revealer set_color *structure_name* *color(int)*
             * /timecore structure_revealer set_visible_through_blocks *true|false*
             */
            return Commands.literal("structure_revealer")
                    .then(Commands.literal("set_color")
                            .then(Commands.argument("structure", StructureArgument.create())
                                    .then(Commands.argument("color", ColorArgument.color())
                                            .executes(context -> setStructureColor(context.getArgument("structure", Structure.class), context.getArgument("color", TextFormatting.class)))
                                    )
                            )
                    ).then(Commands.literal("set_visible_through_blocks")
                            .then(Commands.argument("boolean", BoolArgumentType.bool())
                                    .executes(context -> setVisibleThroughBlocks(context.getArgument("boolean", Boolean.class)))
                            )
                    );
        }

        public static int setStructureColor(Structure<?> structure, TextFormatting color) throws CommandSyntaxException {
            doForRevealer(revealer -> revealer.structureRenderer.setStructureColor(structure.getRegistryName(), color.getColor()));
            return -1;
        }

        public static int setVisibleThroughBlocks(boolean isVisibleThroughBlocks) throws CommandSyntaxException {
            doForRevealer(revealer -> revealer.structureRenderer.setVisibleThroughBlocks(isVisibleThroughBlocks));

            return -1;
        }
    }
}
