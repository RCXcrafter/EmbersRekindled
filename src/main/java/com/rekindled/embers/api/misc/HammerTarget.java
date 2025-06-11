package com.rekindled.embers.api.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class HammerTarget {
	public BlockPos pos;
	public Direction face;

	public HammerTarget(BlockPos pos, Direction face) {
		this.pos = pos;
		this.face = face;
	}
}
