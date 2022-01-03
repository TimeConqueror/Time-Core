package ru.timeconqueror.timecore.tests

import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.Material
import ru.timeconqueror.timecore.TimeCore
import ru.timeconqueror.timecore.api.client.resource.BlockStateResource
import ru.timeconqueror.timecore.api.registry.BlockRegister
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable.InitMethod
import ru.timeconqueror.timecore.api.registry.util.BlockPropsFactory
import ru.timeconqueror.timecore.api.registry.util.invoke

class CompanionBlockRegistry {
    companion object {
        @AutoRegistrable
        private val REGISTER = BlockRegister(TimeCore.MODID)

        @InitMethod
        private fun register() {
            val propsCreator = BlockPropsFactory {
                BlockBehaviour.Properties.of(Material.STONE)
            }

            REGISTER {
                "kotlin_companion_test" represents {
                    Block(propsCreator.create())
                } with {
                    name("Companion Test")
                    defaultBlockItem(CreativeModeTab.TAB_MISC)
                    state(BlockStateResource.fromBuilder(BlockStateResource.Builder.create()))
                }
            }
        }
    }
}