package ru.timeconqueror.timecore.api.common.world.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Random;

public abstract class TunedTemplateStructurePiece extends TemplateStructurePiece {
    private final ResourceLocation templateLocation;

    protected TunedTemplateStructurePiece(StructurePieceType type, StructureManager templateManager, ResourceLocation templateLocation, BlockPos pos) {
        super(type, 0/*genDepth*/);
        this.templateLocation = templateLocation;
        this.templatePosition = pos;
        loadTemplate(templateManager);
    }

    protected TunedTemplateStructurePiece(StructurePieceType type, StructureManager templateManager, CompoundTag nbt) {
        super(type, nbt);
        this.templateLocation = new ResourceLocation(nbt.getString("template"));
        loadTemplate(templateManager);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tagCompound) {
        super.addAdditionalSaveData(tagCompound);
        tagCompound.putString("template", this.templateLocation.toString());
    }

    @Override
    protected void handleDataMarker(String function, BlockPos pos, ServerLevelAccessor worldIn, Random rand, BoundingBox sbb) {

    }

    private void loadTemplate(StructureManager templateManager) {
        StructureTemplate template = templateManager.getOrCreate(this.templateLocation);
        StructurePlaceSettings placementsettings = makePlacementSettings();
        this.setup(template, this.templatePosition, placementsettings);
    }

    protected abstract StructurePlaceSettings makePlacementSettings();
}