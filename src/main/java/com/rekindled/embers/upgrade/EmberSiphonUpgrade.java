package com.rekindled.embers.upgrade;

import com.rekindled.embers.blockentity.CopperChargerBlockEntity;

import net.minecraft.world.level.block.entity.BlockEntity;

public class EmberSiphonUpgrade extends DefaultUpgradeProvider {

	public EmberSiphonUpgrade(BlockEntity tile) {
		super("ember_siphon", tile);
	}

	@Override
	public int getLimit(BlockEntity tile) {
		return tile instanceof CopperChargerBlockEntity ? 1 : 0;
	}

	@Override
	public double getSpeed(BlockEntity tile, double speed) {
		return speed * -1;
	}
}
