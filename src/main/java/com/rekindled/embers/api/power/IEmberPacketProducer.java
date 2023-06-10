package com.rekindled.embers.api.power;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public interface IEmberPacketProducer {
	void setTargetPosition(BlockPos pos, Direction side);

	Direction getEmittingDirection(Direction side);
}
