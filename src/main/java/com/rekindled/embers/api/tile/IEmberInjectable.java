package com.rekindled.embers.api.tile;

import net.minecraft.world.level.block.entity.BlockEntity;

public interface IEmberInjectable {

	void inject(BlockEntity injector, double ember);

	default boolean isValid() {
		return true;
	}
}
