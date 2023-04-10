package com.rekindled.embers.api.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public interface IPipeConnection {

	boolean connectPipe(BlockState state, Direction direction);
}
