//package ru.timeconqueror.timecore.api.common.command.argument;
// TODO port?
//import com.google.common.collect.Lists;
//import com.mojang.brigadier.StringReader;
//import com.mojang.brigadier.arguments.ArgumentType;
//import com.mojang.brigadier.context.CommandContext;
//import com.mojang.brigadier.exceptions.CommandSyntaxException;
//import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
//import com.mojang.brigadier.suggestion.Suggestions;
//import com.mojang.brigadier.suggestion.SuggestionsBuilder;
//import net.minecraft.commands.SharedSuggestionProvider;
//import net.minecraft.network.chat.TranslatableComponent;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.level.levelgen.feature.StructureFeature;
//import net.minecraftforge.registries.ForgeRegistries;
//import ru.timeconqueror.timecore.TimeCore;
//
//import java.util.Collection;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//
//public class StructureArgument implements ArgumentType<StructureFeature<?>> {
//    public static final List<String> EXAMPLES = Lists.newArrayList(StructureFeature.WOODLAND_MANSION.getRegistryName().toString(), StructureFeature.IGLOO.getRegistryName().toString());
//    public static final SimpleCommandExceptionType UNKNOWN = new SimpleCommandExceptionType(new TranslatableComponent("argument." + TimeCore.MODID + ".structure.unknown"));
//
//    public static StructureArgument create() {
//        return new StructureArgument();
//    }
//
//    @Override
//    public StructureFeature<?> parse(StringReader reader) throws CommandSyntaxException {
//        ResourceLocation structureName = ResourceLocation.read(reader);
//
//        StructureFeature<?> structure = ForgeRegistries.STRUCTURE_FEATURES.getValue(structureName);
//
//        if (structure != null) {
//            return structure;
//        } else throw UNKNOWN.createWithContext(reader);
//    }
//
//    @Override
//    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
//        return SharedSuggestionProvider.suggest(ForgeRegistries.STRUCTURE_FEATURES.getValues().stream()
//                .map(structure -> structure.getRegistryName().toString()), builder);
//    }
//
//    @Override
//    public Collection<String> getExamples() {
//        return EXAMPLES;
//    }
//}
