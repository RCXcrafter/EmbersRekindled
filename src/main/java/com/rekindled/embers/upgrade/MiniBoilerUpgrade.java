package com.rekindled.embers.upgrade;

import java.util.List;

import com.rekindled.embers.api.event.EmberEvent;
import com.rekindled.embers.api.event.UpgradeEvent;
import com.rekindled.embers.api.tile.IMechanicallyPowered;
import com.rekindled.embers.api.upgrades.IUpgradeProvider;
import com.rekindled.embers.blockentity.MiniBoilerBlockEntity;

import net.minecraft.world.level.block.entity.BlockEntity;

public class MiniBoilerUpgrade extends DefaultUpgradeProvider {

	public MiniBoilerUpgrade(BlockEntity tile) {
		super("mini_boiler", tile);
	}

	boolean active;
	double heat;

	@Override
	public int getLimit(BlockEntity tile) {
		return tile instanceof IMechanicallyPowered ? 0 : 4;
	}

	@Override
	public void throwEvent(BlockEntity tile, UpgradeEvent event) {
		if (event instanceof EmberEvent) {
			setHeat(((EmberEvent) event).getAmount());
		}
	}

	public void setHeat(double heat) {
		this.heat = heat;
		this.active = true;
	}

	@Override
	public boolean doWork(BlockEntity tile, List<IUpgradeProvider> upgrades) {
		if (active) {
			if (this.tile instanceof MiniBoilerBlockEntity)
				((MiniBoilerBlockEntity) this.tile).boil(heat);
			active = false;
		}

		return false; //No cancel
	}
}
