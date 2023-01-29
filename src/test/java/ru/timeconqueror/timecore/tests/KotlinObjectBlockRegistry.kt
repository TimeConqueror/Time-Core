package ru.timeconqueror.timecore.tests

import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.Material
import ru.timeconqueror.timecore.TimeCore
import ru.timeconqueror.timecore.api.client.resource.BlockStateResource
import ru.timeconqueror.timecore.api.registry.BlockRegister
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable.Init
import ru.timeconqueror.timecore.api.registry.util.BlockPropsFactory
import ru.timeconqueror.timecore.api.registry.util.invoke

@AutoRegistrable.Entries(value = TimeCore.MODID, registryKey = "block")
object KotlinObjectBlockRegistry {
    @AutoRegistrable
    private val REGISTER = BlockRegister(TimeCore.MODID)

    lateinit var KOTLIN_OBJECT_TEST: Block

    @Init
    private fun register() {
        val propsCreator = BlockPropsFactory {
            BlockBehaviour.Properties.of(Material.STONE)
        }

        REGISTER {
            "kotlin_object_test" represents {
                Block(propsCreator.create())
            } with {
                name("Kotlin Object Test")
                defaultBlockItem(CreativeModeTabs.TOOLS_AND_UTILITIES)
                state(BlockStateResource.fromBuilder(BlockStateResource.Builder.create()))
            }
        }
    }
}