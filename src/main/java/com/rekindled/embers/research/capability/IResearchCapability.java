package com.rekindled.embers.research.capability;

import java.util.Map;

import net.minecraft.nbt.CompoundTag;

public interface IResearchCapability {
	void setCheckmark(String research, boolean checked);
	boolean isChecked(String research);
	Map<String,Boolean> getCheckmarks();
	void writeToNBT(CompoundTag tag);
	void readFromNBT(CompoundTag tag);
}
