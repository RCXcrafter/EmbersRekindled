package com.rekindled.embers.upgrade;

import java.util.List;

import com.rekindled.embers.api.upgrades.IUpgradeProvider;
import com.rekindled.embers.blockentity.ClockworkAttenuatorBlockEntity;

import net.minecraft.world.level.block.entity.BlockEntity;


public class UpgradeClockworkAttenuator extends DefaultUpgradeProvider {

	public UpgradeClockworkAttenuator(BlockEntity tile) {
		super("clockwork_attenuator", tile);
	}

	@Override
	public int getLimit(BlockEntity tile) {
		return 1;
	}

	@Override
	public double getSpeed(BlockEntity tile, double speed) {
		return speed * getSpeedModifier();
	}

	private double getSpeedModifier() {
		if (this.tile instanceof ClockworkAttenuatorBlockEntity)
			return ((ClockworkAttenuatorBlockEntity) this.tile).getSpeed();
		return 0;
	}

	@Override
	public boolean doWork(BlockEntity tile, List<IUpgradeProvider> upgrades) {
		return getSpeedModifier() == 0;
	}
}
