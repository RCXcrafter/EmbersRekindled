package com.rekindled.embers.util;

import com.rekindled.embers.Embers;

import net.minecraft.client.resources.language.I18n;

public class FluidAmounts {

	public static final int NUGGET_AMOUNT = 10;
	public static final int INGOT_AMOUNT = NUGGET_AMOUNT * 9;
	public static final int BLOCK_AMOUNT = INGOT_AMOUNT * 9;
	public static final int RAW_AMOUNT = NUGGET_AMOUNT * 12;
	public static final int ORE_AMOUNT = RAW_AMOUNT * 2;
	public static final int RAW_BLOCK_AMOUNT = RAW_AMOUNT * 9;
	public static final int PLATE_AMOUNT = INGOT_AMOUNT;
	public static final int GEAR_AMOUNT = INGOT_AMOUNT * 2;

	public static String getIngotTooltip(int amount) {
		String tooltip = "";
		String ingots = null;
		String nuggets = null;
		String mb = null;

		if (amount >= INGOT_AMOUNT) {
			int count = amount / INGOT_AMOUNT;
			if (count == 1) {
				ingots = I18n.get(Embers.MODID + ".tooltip.fluiddial.ingot");
			} else {
				ingots = I18n.get(Embers.MODID + ".tooltip.fluiddial.ingots", count);
			}
		}
		if (amount % INGOT_AMOUNT >= NUGGET_AMOUNT) {
			int count = (amount % INGOT_AMOUNT) / NUGGET_AMOUNT;
			if (count == 1) {
				nuggets = I18n.get(Embers.MODID + ".tooltip.fluiddial.nugget");
			} else {
				nuggets = I18n.get(Embers.MODID + ".tooltip.fluiddial.nuggets", count);
			}
		}
		if (amount % NUGGET_AMOUNT > 0) {
			int count = amount % NUGGET_AMOUNT;
			mb = I18n.get(Embers.MODID + ".tooltip.fluiddial.millibucket", count);
		}

		if (ingots == null && nuggets == null) {
			return "";
		}
		if (ingots != null) {
			tooltip += ingots;
		}
		if (nuggets != null) {
			if (tooltip.isEmpty()) {
				tooltip += nuggets;
			} else {
				tooltip = I18n.get(Embers.MODID + ".tooltip.fluiddial.separator", tooltip, nuggets);
			}
		}
		if (mb != null) {
			tooltip = I18n.get(Embers.MODID + ".tooltip.fluiddial.separator", tooltip, mb);
		}

		return tooltip;
	}
}
