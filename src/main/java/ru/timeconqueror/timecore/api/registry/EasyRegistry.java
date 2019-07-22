package ru.timeconqueror.timecore.api.registry;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class EasyRegistry {
    private final String modid;
    private CreativeTabs tab;

    /**
     * @param modid the part of registry object name, that will be added in places like translationKey.
     *              <p>Example: {@code prefix} == "timecore", blockName = "test_block",
     *              <p>Translation Key Sum: "tile.timecore:test_block.name"
     * @param tabIn CreativeTab, where items and blocks will be placed.
     *              <p>
     *              YOU MUST REGISTER BLOCKS ITEMS, TILEENTITIES AND TESRS ONLY DURING {@link FMLPreInitializationEvent} event
     *              YOU MUST REGISTER SOUNDS ONLY DURING {@link FMLPostInitializationEvent} evenT
     *              OR VIA SPECIAL REGISTRY EVENTS!
     */
    public EasyRegistry(String modid, @Nullable CreativeTabs tabIn) {
        this.modid = modid;
        this.tab = tabIn;
    }

    /**
     * Changes tab for future registered items and blocks.
     */
    public EasyRegistry setTab(@Nullable CreativeTabs tab) {
        this.tab = tab;
        return this;
    }

    public void registerBlock(Block block, String name) {
        registerBlock(block, name, name);
    }

    public void registerBlock(Block block, String unlocalizedName, String registryName) {
        ForgeRegistries.BLOCKS.register(block.setTranslationKey(modid + "." + unlocalizedName).setRegistryName(modid, registryName));
    }

    public void registerBlockWithItem(Block block, String name, ItemBlock itemBlock) {
        registerBlockWithItem(block, name, name, itemBlock);
    }

    public void registerBlockWithItem(Block block, String unlocalizedName, String registryName, ItemBlock itemBlock) {
        if (tab != null) {
            block.setCreativeTab(tab);
        }
        ForgeRegistries.BLOCKS.register(block.setTranslationKey(modid + "." + unlocalizedName).setRegistryName(modid, registryName));
        ForgeRegistries.ITEMS.register(itemBlock.setRegistryName(modid, registryName));
    }

    public void registerItem(Item item, String name) {
        if (tab != null) {
            item.setCreativeTab(tab);
        }
        ForgeRegistries.ITEMS.register(item.setTranslationKey(modid + "." + name).setRegistryName(modid, name));
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockRender(Block block) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName(), "inventory"));
    }

    @SideOnly(Side.CLIENT)
    public void registerItemRender(Item item, int meta, String fileName) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(modid + ":" + fileName, "inventory"));
    }

    @SideOnly(Side.CLIENT)
    public void registerItemRender(Item item) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    public void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String name) {
        GameRegistry.registerTileEntity(tileEntityClass, new ResourceLocation(modid, "te." + name));
    }

    @SideOnly(Side.CLIENT)
    public <T extends TileEntity> void registerTESR(Class<T> tileEntityClass, TileEntitySpecialRenderer<? super T> renderer) {
        ClientRegistry.bindTileEntitySpecialRenderer(tileEntityClass, renderer);
    }


    public SoundEvent registerSound(ResourceLocation rl) {
        SoundEvent soundevent = new SoundEvent(rl);
        soundevent.setRegistryName(rl);
        ForgeRegistries.SOUND_EVENTS.register(soundevent);
        return soundevent;
    }
}
