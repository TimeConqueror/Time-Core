package ru.timeconqueror.timecore.common.registry.block;

import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import ru.timeconqueror.timecore.api.ITimeMod;
import ru.timeconqueror.timecore.common.registry.ForgeTimeRegistry;

public abstract class BlockTimeRegistry extends ForgeTimeRegistry<Block> {

    public BlockTimeRegistry(ITimeMod mod) {
        super(mod);
    }

    @SubscribeEvent
    public void regBlocks(RegistryEvent.Register<Block> event) {
        onFireRegistryEvent(event);
    }

    /**
     * Register block with given name.
     *
     * @param name will be used in registry and localization key.
     */
    public BlockWrapper regBlock(Block block, String name) {
        name = name.toLowerCase();
        return new BlockWrapper(block, name);
    }

    public class BlockWrapper extends EntryWrapper {
        public BlockWrapper(Block block, String name) {
            super(block, name);
        }
    }
}
