package ru.timeconqueror.timecore.client.command;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.client.IClientCommand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.client.obj.generator.ObjFileGenerator;
import ru.timeconqueror.timecore.reference.FileReferences;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandClientMain extends CommandBase implements IClientCommand {

    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }

    @Override
    public String getName() {
        return "timecore";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "command.timecore.main.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            throw new WrongUsageException(getUsage(sender));
        } else {
            if (args[0].equals(Commands.GENERATE_OBJ.getName())) {
                generateObj(server, sender, args);
            } else if (args[0].equals(Commands.HELP.getName())) {
                sender.sendMessage(new TextComponentTranslation("command.timecore.help.msg"));
                for (Commands value : Commands.values()) {
                    sender.sendMessage(new TextComponentTranslation(value.getUsage()));
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void generateObj(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            throw new WrongUsageException(TimeCore.MODID + ".command." + Commands.GENERATE_OBJ + ".usage");
        }

        String entityName = args[1];
        NBTTagCompound tagCompound = new NBTTagCompound();

        if (args.length >= 3) {
            String s1 = buildString(args, 2);

            try {
                tagCompound = JsonToNBT.getTagFromJson(s1);
            } catch (NBTException nbtexception) {
                throw new CommandException("commands.summon.tagError", nbtexception.getMessage());
            }
        }
        tagCompound.setString("id", entityName);

        Entity entity = createEntityFromNBT(tagCompound, Minecraft.getMinecraft().world);
        if (entity == null) {
            throw new CommandException(TimeCore.MODID + ".command." + Commands.GENERATE_OBJ + ".unknown");
        }

        Render<Entity> renderSimple = Minecraft.getMinecraft().getRenderManager().getEntityClassRenderObject(entity.getClass());
        if (!(renderSimple instanceof RenderLivingBase)) {
            entity.setDead();
            throw new CommandException(TimeCore.MODID + ".command." + Commands.GENERATE_OBJ + ".not_living");
        } else {
            RenderLivingBase renderLiving = (RenderLivingBase) renderSimple;
            ModelBase model = renderLiving.getMainModel();

            entity.setDead();

            new ObjFileGenerator().create(FileReferences.TIMECORE_GENOBJ_DIR, model, entity.getName());
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, Commands.getCommands());
        }

        if (args[0].equals(Commands.GENERATE_OBJ.getName()) && args.length == 2) {
            return getListOfStringsMatchingLastWord(args, EntityList.getEntityNameList());
        }

        return Collections.emptyList();
    }

    @Nullable
    private Entity createEntityFromNBT(NBTTagCompound compound, World worldIn) {
        try {
            return EntityList.createEntityFromNBT(compound, worldIn);
        } catch (RuntimeException var3) {
            return null;
        }
    }

    private enum Commands {
        GENERATE_OBJ("genobj"),
        HELP("help");

        private String name;
        private String usage;

        Commands(String name) {
            this.name = name;
            this.usage = "command." + TimeCore.MODID + "." + name + ".usage";
        }

        public static String[] getCommands() {
            String[] commands = new String[values().length];
            for (int i = 0; i < values().length; i++) {
                commands[i] = values()[i].name;
            }

            return commands;
        }

        public String getUsage() {
            return usage;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
