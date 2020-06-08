package ru.timeconqueror.timecore.common.command.argument;

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
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import ru.timeconqueror.timecore.TimeCore;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class StructureArgument implements ArgumentType<Structure<?>> {
    public static final List<String> EXAMPLES = Lists.newArrayList(Feature.WOODLAND_MANSION.getRegistryName().toString(), Feature.IGLOO.getRegistryName().toString());
    public static final SimpleCommandExceptionType IS_FEATURE = new SimpleCommandExceptionType(new TranslationTextComponent("argument." + TimeCore.MODID + ".structure.is_feature"));
    public static final SimpleCommandExceptionType UNKNOWN = new SimpleCommandExceptionType(new TranslationTextComponent("argument." + TimeCore.MODID + ".structure.unknown"));

    @Override
    public Structure<?> parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation structureName = ResourceLocation.read(reader);

        Feature<?> feature = ForgeRegistries.FEATURES.getValue(structureName);

        if (feature != null) {
            if (feature instanceof Structure<?>) {
                return (Structure<?>) feature;
            } else throw IS_FEATURE.createWithContext(reader);
        } else throw UNKNOWN.createWithContext(reader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.func_212476_a/*suggest*/(ForgeRegistries.FEATURES.getValues().stream()
                .filter(feature -> feature instanceof Structure<?>)
                .map(ForgeRegistryEntry::getRegistryName), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static StructureArgument create() {
        return new StructureArgument();
    }
}
