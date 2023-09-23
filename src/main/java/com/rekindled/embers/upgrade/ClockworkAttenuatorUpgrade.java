package com.rekindled.embers.upgrade;

import java.util.List;

import com.rekindled.embers.Embers;
import com.rekindled.embers.api.upgrades.UpgradeContext;
import com.rekindled.embers.blockentity.ClockworkAttenuatorBlockEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;


public class ClockworkAttenuatorUpgrade extends DefaultUpgradeProvider {

	public ClockworkAttenuatorUpgrade(BlockEntity tile) {
		super(new ResourceLocation(Embers.MODID, "clockwork_attenuator"), tile);
	}

	@Override
	public int getPriority() {
		return -100; //before everything else
	}

	@Override
	public int getLimit(BlockEntity tile) {
		return 1;
	}

	@Override
	public double getSpeed(BlockEntity tile, double speed, int distance, int count) {
		return speed * getSpeedModifier();
	}

	private double getSpeedModifier() {
		if (this.tile instanceof ClockworkAttenuatorBlockEntity)
			return ((ClockworkAttenuatorBlockEntity) this.tile).getSpeed();
		return 0;
	}

	@Override
	public boolean doWork(BlockEntity tile, List<UpgradeContext> upgrades, int distance, int count) {
		return getSpeedModifier() == 0;
	}
}
