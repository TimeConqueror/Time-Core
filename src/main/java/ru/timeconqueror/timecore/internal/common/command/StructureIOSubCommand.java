package ru.timeconqueror.timecore.internal.common.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.phys.Vec3;
import ru.timeconqueror.timecore.api.util.EnvironmentUtils;
import ru.timeconqueror.timecore.structureio.ExtendedStructureTemplate;
import ru.timeconqueror.timecore.structureio.StructureIO;

import java.nio.file.Path;

public class StructureIOSubCommand {
    private static final StructureIO STRUCTURE_IO = StructureIO.INSTANCE;
    private static final Path STRUCTURE_DIR = EnvironmentUtils.getGameDir().resolve("structures");

    public static Path resolvePath(String relPath) {
        return STRUCTURE_DIR.resolve(Path.of(relPath + ".dat"));
    }

    private static int save(CommandSourceStack commandSource, BlockPos from, BlockPos to, String relPath, boolean includeEntities) {
        Vec3 sourceVec = commandSource.getPosition();
        BlockPos sourcePos = BlockPos.containing(sourceVec);

        ServerLevel level = commandSource.getLevel();
        Path fullPath = resolvePath(relPath);
        STRUCTURE_IO.save(level, from, to, fullPath, includeEntities, Blocks.AIR, sourcePos);

        commandSource.sendSuccess(() -> Component.literal("Structure saved to" + fullPath), false);

        return Command.SINGLE_SUCCESS;
    }

    private static int generate(CommandSourceStack commandSource, String pathStr, BlockPos at, boolean relativePath) {
        pathStr = pathStr + ".dat";

        ServerLevel level = commandSource.getLevel();
        Path path;
        if (relativePath) {
            path = resolvePath(pathStr);
        } else {
            path = Path.of(pathStr);
        }

        ExtendedStructureTemplate template = STRUCTURE_IO.getOrLoadTemplateFromFile(path.toFile());
        STRUCTURE_IO.generate(template, level, at, new StructurePlaceSettings());

        return Command.SINGLE_SUCCESS;
    }

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("structureio")
                .requires(css -> css.hasPermission(4))
                .then(Commands.literal("save")
                        .then(Commands.argument("from", BlockPosArgument.blockPos())
                                .then(Commands.argument("to", BlockPosArgument.blockPos())
                                        .then(Commands.argument("path", StringArgumentType.string())
                                                .executes(ctx -> {
                                                    BlockPos from = BlockPosArgument.getBlockPos(ctx, "from");
                                                    BlockPos to = BlockPosArgument.getBlockPos(ctx, "to");
                                                    String path = StringArgumentType.getString(ctx, "path");

                                                    return save(ctx.getSource(), from, to, path, false);
                                                })
                                                .then(Commands.argument("includeEntities", BoolArgumentType.bool())
                                                        .executes(ctx -> {
                                                            BlockPos from = BlockPosArgument.getBlockPos(ctx, "from");
                                                            BlockPos to = BlockPosArgument.getBlockPos(ctx, "to");
                                                            String path = StringArgumentType.getString(ctx, "path");
                                                            boolean includeEntities = BoolArgumentType.getBool(ctx, "includeEntities");
                                                            return save(ctx.getSource(), from, to, path, includeEntities);
                                                        })
                                                )
                                        )
                                )
                        )
                )
                .then(Commands.literal("generate")
                        .then(Commands.argument("from_path", StringArgumentType.string())
                                .then(Commands.argument("at", BlockPosArgument.blockPos())
                                        .executes(ctx -> {
                                            String fromPath = StringArgumentType.getString(ctx, "from_path");
                                            BlockPos at = BlockPosArgument.getBlockPos(ctx, "at");

                                            return generate(ctx.getSource(), fromPath, at, true);
                                        })
                                        .then(Commands.argument("is_path_relative", BoolArgumentType.bool())
                                                .executes(ctx -> {
                                                    String fromPath = StringArgumentType.getString(ctx, "from_path");
                                                    BlockPos at = BlockPosArgument.getBlockPos(ctx, "at");
                                                    boolean relativePath = BoolArgumentType.getBool(ctx, "is_path_relative");

                                                    return generate(ctx.getSource(), fromPath, at, relativePath);
                                                })
                                        )
                                )
                        )
                )
                .then(Commands.literal("directory")
                        .then(Commands.literal("get")
                                .executes(ctx -> {
                                    ctx.getSource().sendSuccess(() -> Component.literal("Structure Directory: " + STRUCTURE_DIR.toAbsolutePath()), false);
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                );
    }
}