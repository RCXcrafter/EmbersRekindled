package com.rekindled.embers.api.block;

import com.rekindled.embers.blockentity.PipeBlockEntityBase.PipeConnection;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public interface IPipeConnection {

	PipeConnection getPipeConnection(BlockState state, Direction direction);
}
