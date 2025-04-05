package com.rekindled.embers.upgrade;

import java.util.List;

import com.rekindled.embers.ConfigManager;
import com.rekindled.embers.Embers;
import com.rekindled.embers.api.event.HeatCoilVisualEvent;
import com.rekindled.embers.api.event.UpgradeEvent;
import com.rekindled.embers.api.upgrades.UpgradeContext;
import com.rekindled.embers.blockentity.HearthCoilBlockEntity;
import com.rekindled.embers.util.EmbersColors;
import com.rekindled.embers.util.Misc;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;

public class HeatInsulationUpgrade extends DefaultUpgradeProvider {

	public HeatInsulationUpgrade(BlockEntity tile) {
		super(new ResourceLocation(Embers.MODID, "heat_insulation"), tile);
	}

	@Override
	public int getPriority() {
		return -90; //after the clockwork attenuator
	}

	@Override
	public int getLimit(BlockEntity tile) {
		if (tile instanceof HearthCoilBlockEntity)
			return Integer.MAX_VALUE;
		return 0;
	}

	@Override
	public double getOtherParameter(BlockEntity tile, String type, double value, int distance, int count) {
		if (distance == 0)
			distance = 1;
		distance *= distance;
		if (type.equals("max_heat"))
			return value + 75.0 / distance;
		if (type.equals("cooling_speed"))
			return value * (1.0 - 0.3 / distance);
		return value;
	}

	@Override
	public double transformEmberConsumption(BlockEntity tile, double ember, int distance, int count) {
		if (distance == 0)
			distance = 1;
		distance *= distance;
		return ember * (1.0 - 0.2 / distance);
	}

	@Override
	public void throwEvent(BlockEntity tile, List<UpgradeContext> upgrades, UpgradeEvent event, int distance, int count) {
		if (event instanceof HeatCoilVisualEvent visualEvent && tile instanceof HearthCoilBlockEntity) {
			double heat = ((HearthCoilBlockEntity) tile).heat;
			double overheat = heat - ConfigManager.HEARTH_COIL_MAX_HEAT.get();
			visualEvent.setColor(Misc.lerpColor(visualEvent.getColor(), EmbersColors.OVERHEAT, (float) Mth.clamp(overheat / 300.0, 0, 1)));
			visualEvent.setVerticalSpeed((float) Mth.clampedLerp(visualEvent.getVerticalSpeed(), Math.max(visualEvent.getVerticalSpeed(), 0.9), overheat / 300.0));
		}
	}
}
