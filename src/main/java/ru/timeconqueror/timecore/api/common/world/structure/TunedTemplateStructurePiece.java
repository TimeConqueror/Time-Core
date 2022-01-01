package ru.timeconqueror.timecore.api.common.world.structure;

//FIXME PORT
//public abstract class TunedTemplateStructurePiece extends TemplateStructurePiece {
//    private final ResourceLocation templateLocation;
//
//    protected TunedTemplateStructurePiece(StructurePieceType type, StructureManager templateManager, ResourceLocation templateLocation, BlockPos pos) {
//        super(type, 0/*genDepth*/);
//        this.templateLocation = templateLocation;
//        this.templatePosition = pos;
//        loadTemplate(templateManager);
//    }
//
//    protected TunedTemplateStructurePiece(StructurePieceType type, StructureManager templateManager, CompoundTag nbt) {
//        super(type, nbt);
//        this.templateLocation = new ResourceLocation(nbt.getString("template"));
//        loadTemplate(templateManager);
//    }
//
//    @Override
//    protected void addAdditionalSaveData(CompoundTag tagCompound) {
//        super.addAdditionalSaveData(tagCompound);
//        tagCompound.putString("template", this.templateLocation.toString());
//    }
//
//    @Override
//    protected void handleDataMarker(String function, BlockPos pos, ServerLevelAccessor worldIn, Random rand, BoundingBox sbb) {
//
//    }
//
//    private void loadTemplate(StructureManager templateManager) {
//        StructureTemplate template = templateManager.getOrCreate(this.templateLocation);
//        StructurePlaceSettings placementsettings = makePlacementSettings();
//        this.setup(template, this.templatePosition, placementsettings);
//    }
//
//    protected abstract StructurePlaceSettings makePlacementSettings();
//}