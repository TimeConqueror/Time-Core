package example;

import com.timeconqueror.timecore.TimeCore;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;

public class ModEntities {

    public static void register() {
        int id = 0;

        registerEntity("phoenix", EntityPhoenix.class, id++, 80, 3, true);
    }

    /**
     * Register Render Class for this Entity
     */
    @SideOnly(Side.CLIENT)
    public static void registerRenders() {
        RenderingRegistry.registerEntityRenderingHandler(EntityPhoenix.class, new RenderPhoenix());
    }

    /**
     * Register entity without spawn egg.
     */
    public static void registerEntity(String name, Class<? extends Entity> entityClass, int id, int trackingRange, int updateFrequency, boolean sendVelocityUpdates) {
        EntityRegistry.registerModEntity(entityClass, name, id, TimeCore.instance, trackingRange, updateFrequency, sendVelocityUpdates);
    }

    public static void registerSpawnEggs() {
//        Item itemSpawnEgg = new ModItemMonsterPlacer("phoenix", 0x000000, 0xFFFFFF)
//                .setUnlocalizedName("spawn_egg")
//                .setTextureName(TimeCore.MODID + ":spawn_egg");
//        GameRegistry.registerItem(itemSpawnEgg, "spawn_egg", TimeCore.MODID);
    }
}
