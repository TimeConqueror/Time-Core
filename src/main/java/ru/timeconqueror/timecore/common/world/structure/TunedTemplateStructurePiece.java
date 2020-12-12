package ru.timeconqueror.timecore.common.world.structure;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.Random;

public abstract class TunedTemplateStructurePiece extends TemplateStructurePiece {
    private final ResourceLocation templateLocation;

    protected TunedTemplateStructurePiece(IStructurePieceType type, TemplateManager templateManager, ResourceLocation templateLocation, BlockPos pos) {
        super(type, 0/*genDepth*/);
        this.templateLocation = templateLocation;
        this.templatePosition = pos;
        loadTemplate(templateManager);
    }

    protected TunedTemplateStructurePiece(IStructurePieceType type, TemplateManager templateManager, CompoundNBT nbt) {
        super(type, nbt);
        this.templateLocation = new ResourceLocation(nbt.getString("template"));
        loadTemplate(templateManager);
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT tagCompound) {
        super.addAdditionalSaveData(tagCompound);
        tagCompound.putString("template", this.templateLocation.toString());
    }

    @Override
    protected void handleDataMarker(String function, BlockPos pos, IServerWorld worldIn, Random rand, MutableBoundingBox sbb) {

    }

    private void loadTemplate(TemplateManager templateManager) {
        Template template = templateManager.getOrCreate(this.templateLocation);
        PlacementSettings placementsettings = makePlacementSettings();
        this.setup(template, this.templatePosition, placementsettings);
    }

    protected abstract PlacementSettings makePlacementSettings();
}