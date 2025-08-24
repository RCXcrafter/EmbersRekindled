package com.rekindled.embers.api.power;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public interface IEmberPacketProducer {
	Vec3 getEmittingDirection(Direction side);

	BlockPos getTarget(Direction side);
}
