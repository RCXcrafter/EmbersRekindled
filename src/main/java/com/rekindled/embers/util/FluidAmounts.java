package com.rekindled.embers.util;

import com.rekindled.embers.ConfigManager;
import com.rekindled.embers.Embers;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class FluidAmounts {

	public static int nuggetValue() {
		return ConfigManager.NUGGET_FLUID_VALUE.get();
	}

	public static int ingotValue() {
		return ConfigManager.NUGGET_FLUID_VALUE.get() * 9;
	}

	public static MutableComponent getIngotTooltip(int amount) {
		MutableComponent tooltip = null;
		MutableComponent ingots = null;
		MutableComponent nuggets = null;
		MutableComponent mb = null;

		if (amount >= ingotValue()) {
			int count = amount / ingotValue();
			if (count == 1) {
				ingots = Component.translatable(Embers.MODID + ".tooltip.fluiddial.ingot");
			} else {
				ingots = Component.translatable(Embers.MODID + ".tooltip.fluiddial.ingots", count);
			}
		}
		if (amount % ingotValue() >= nuggetValue()) {
			int count = (amount % ingotValue()) / nuggetValue();
			if (count == 1) {
				nuggets = Component.translatable(Embers.MODID + ".tooltip.fluiddial.nugget");
			} else {
				nuggets = Component.translatable(Embers.MODID + ".tooltip.fluiddial.nuggets", count);
			}
		}
		if (amount % nuggetValue() > 0) {
			int count = amount % nuggetValue();
			mb = Component.translatable(Embers.MODID + ".tooltip.fluiddial.millibucket", count);
		}

		if (ingots == null && nuggets == null) {
			return Component.empty();
		}
		if (ingots != null) {
			tooltip = ingots;
		}
		if (nuggets != null) {
			if (tooltip == null) {
				tooltip = nuggets;
			} else {
				tooltip = Component.translatable(Embers.MODID + ".tooltip.fluiddial.separator", tooltip, nuggets);
			}
		}
		if (mb != null) {
			tooltip = Component.translatable(Embers.MODID + ".tooltip.fluiddial.separator", tooltip, mb);
		}

		return tooltip;
	}
}
