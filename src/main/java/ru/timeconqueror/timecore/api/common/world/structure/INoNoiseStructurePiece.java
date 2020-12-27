package ru.timeconqueror.timecore.api.common.world.structure;

import ru.timeconqueror.timecore.api.registry.StructureRegister.StructureRegisterChain;

/**
 * Noise smoother won't apply on structure pieces, which implement this interface.
 * Only works with structures with enabled {@link StructureRegisterChain#transformsSurroundingLand()}
 */
public interface INoNoiseStructurePiece {
}
