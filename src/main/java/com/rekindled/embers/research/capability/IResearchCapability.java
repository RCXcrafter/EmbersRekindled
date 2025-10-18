package com.rekindled.embers.research.capability;

import java.util.Map;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public interface IResearchCapability {
	void setCheckmark(ResourceLocation research, boolean checked);
	boolean isChecked(ResourceLocation research);
	Map<ResourceLocation, Boolean> getCheckmarks();
	void writeToNBT(CompoundTag tag);
	void readFromNBT(CompoundTag tag);
}
