package com.rekindled.embers.api.tile;

import net.minecraft.world.level.block.entity.BlockEntity;

public interface IHammerable {
	void onHit(BlockEntity hammer);

	default boolean isValid() {
		return true;
	}
}
