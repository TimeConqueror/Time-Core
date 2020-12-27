package ru.timeconqueror.timecore.api.common.command.argument;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.registries.ForgeRegistries;
import ru.timeconqueror.timecore.TimeCore;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class StructureArgument implements ArgumentType<Structure<?>> {
    public static final List<String> EXAMPLES = Lists.newArrayList(Structure.WOODLAND_MANSION.getRegistryName().toString(), Structure.IGLOO.getRegistryName().toString());
    public static final SimpleCommandExceptionType UNKNOWN = new SimpleCommandExceptionType(new TranslationTextComponent("argument." + TimeCore.MODID + ".structure.unknown"));

    public static StructureArgument create() {
        return new StructureArgument();
    }

    @Override
    public Structure<?> parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation structureName = ResourceLocation.read(reader);

        Structure<?> structure = ForgeRegistries.STRUCTURE_FEATURES.getValue(structureName);

        if (structure != null) {
            return structure;
        } else throw UNKNOWN.createWithContext(reader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggest(ForgeRegistries.STRUCTURE_FEATURES.getValues().stream()
                .map(structure -> structure.getRegistryName().toString()), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
