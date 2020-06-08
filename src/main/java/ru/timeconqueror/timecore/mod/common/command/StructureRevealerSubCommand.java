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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.common.command.argument.StructureArgument;
import ru.timeconqueror.timecore.devtools.StructureRevealer;

import java.util.List;
import java.util.stream.Collectors;

public class StructureRevealerSubCommand {
    public static final SimpleCommandExceptionType REVEALER_DEACTIVATED = new SimpleCommandExceptionType(new TranslationTextComponent("cmd." + TimeCore.MODID + ".structure_revealer.deactivated"));

    public static ArgumentBuilder<CommandSource, ?> register() {
        /**
         * /timecore structure_revealer subscribe *structure_name* *player*|me
         * /timecore structure_revealer unsubscribe *structure_name* *player*|me
         * /timecore structure_revealer unsubscribe_all *player*|me
         * /timecore structure_revealer get_subscriptions *player*|me
         */
        return Commands.literal("structure_revealer")
                .then(Commands.literal("subscribe")
                        .then(Commands.argument("structure", StructureArgument.create())
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(context -> subscribe(context.getSource(), context.getArgument("player", ServerPlayerEntity.class), context.getArgument("structure", Structure.class)))
                                ).executes(context -> subscribe(context.getSource(), context.getSource().asPlayer(), context.getArgument("structure", Structure.class)))
                        )
                ).then(Commands.literal("unsubscribe")
                        .then(Commands.argument("structure", StructureArgument.create())
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(context -> unsubscribe(context.getArgument("player", ServerPlayerEntity.class), context.getArgument("structure", Structure.class)))
                                ).executes(context -> unsubscribe(context.getSource().asPlayer(), context.getArgument("structure", Structure.class)))
                        )
                ).then(Commands.literal("unsubscribe_all")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> unsubscribe(context.getArgument("player", ServerPlayerEntity.class), null))
                        ).executes(context -> unsubscribe(context.getSource().asPlayer(), null))
                ).then(Commands.literal("get_subscriptions")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> getSubscriptions(context.getArgument("player", ServerPlayerEntity.class), context.getSource()))
                        ).executes(context -> getSubscriptions(context.getSource().asPlayer(), context.getSource()))
                );
    }

    private static int subscribe(CommandSource source, ServerPlayerEntity player, Structure<?> structure) throws CommandSyntaxException {
        StructureRevealer instance = checkRevealerActiveness();

        instance.subscribePlayerToStructure(player, structure);

//        source.sendFeedback(new TranslationTextComponent("cmd." + TextFormatting));

        return 1;
    }

    private static int unsubscribe(ServerPlayerEntity player, @Nullable Structure<?> structure) throws CommandSyntaxException {
        StructureRevealer instance = checkRevealerActiveness();

        if (structure == null) {
            instance.unsubscribePlayerFromAllStructures(player);
        } else {
            instance.unsubscribePlayerFromStructure(player, structure);
        }

        return 1;
    }

    private static int getSubscriptions(ServerPlayerEntity player, CommandSource source) throws CommandSyntaxException {
        StructureRevealer instance = checkRevealerActiveness();

        List<ResourceLocation> subscriptions = instance.getSubscriptions(player);

        String listOut = subscriptions.stream()
                .map(ResourceLocation::toString)
                .collect(Collectors.joining(","));

        source.sendFeedback(new TranslationTextComponent("cmd." + TimeCore.MODID + ".structure_revealer.list", player).applyTextStyle(TextFormatting.AQUA)
                .appendSibling(new StringTextComponent("\n"))
                .appendSibling(new StringTextComponent(listOut)), false);

        return 1;
    }

    @NotNull
    private static StructureRevealer checkRevealerActiveness() throws CommandSyntaxException {
        if (StructureRevealer.getInstance() == null) {
            throw REVEALER_DEACTIVATED.create();
        }

        return StructureRevealer.getInstance();
    }

    public static class ClientSubCommand {
        public static ArgumentBuilder<CommandSource, ?> register() {
            /**
             * /timecore structure_revealer set_color *structure_name* *color(int)*
             * /timecore structure_revealer set_visible_through_blocks *true|false*
             */
            return Commands.literal("structure_revealer")
                    .then(Commands.literal("set_color")
                            .then(Commands.argument("structure", StructureArgument.create())
                                    .then(Commands.argument("color", new ColorArgument())
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
            StructureRevealer structureRevealer = checkRevealerActiveness();
            structureRevealer.structureRenderer.setStructureColor(structure.getRegistryName(), color.getColor());
            return -1;
        }

        public static int setVisibleThroughBlocks(boolean isVisibleThroughBlocks) throws CommandSyntaxException {
            StructureRevealer structureRevealer = checkRevealerActiveness();
            structureRevealer.structureRenderer.setVisibleThroughBlocks(isVisibleThroughBlocks);

            return -1;
        }
    }
}
