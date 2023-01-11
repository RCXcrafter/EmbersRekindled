package com.rekindled.embers.research.capability;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.nbt.CompoundTag;

public class DefaultResearchCapability implements IResearchCapability {
	Map<String, Boolean> Checkmarks = new HashMap<>();

	@Override
	public void setCheckmark(String research, boolean checked) {
		Checkmarks.put(research,checked);
	}

	@Override
	public boolean isChecked(String research) {
		return Checkmarks.getOrDefault(research,false);
	}

	@Override
	public Map<String, Boolean> getCheckmarks() {
		return Checkmarks;
	}

	@Override
	public void writeToNBT(CompoundTag tag) {
		CompoundTag checkmarks = new CompoundTag();
		for (Map.Entry<String,Boolean> entry : Checkmarks.entrySet()) {
			checkmarks.putBoolean(entry.getKey(),entry.getValue());
		}
		tag.put("checkmarks",checkmarks);
	}

	@Override
	public void readFromNBT(CompoundTag tag) {
		CompoundTag checkmarks = tag.getCompound("checkmarks");
		Checkmarks.clear();
		for (String key : checkmarks.getAllKeys()) {
			Checkmarks.put(key,checkmarks.getBoolean(key));
		}
	}
}
