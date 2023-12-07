package com.rekindled.embers.augment;

import com.rekindled.embers.Embers;

import net.minecraft.resources.ResourceLocation;

public class CoreAugment extends AugmentBase {

	public CoreAugment() {
		super(new ResourceLocation(Embers.MODID, "core"), 0.0);
	}

	@Override
	public boolean countTowardsTotalLevel() {
		return false;
	}

	@Override
	public boolean canRemove() {
		return false;
	}

	@Override
	public boolean shouldRenderTooltip() {
		return false;
	}
}
