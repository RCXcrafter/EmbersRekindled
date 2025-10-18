package com.rekindled.embers.research.capability;

import java.util.HashMap;
import java.util.Map;

import com.rekindled.embers.Embers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class DefaultResearchCapability implements IResearchCapability {
	Map<ResourceLocation, Boolean> checkmarks = new HashMap<>();

	@Override
	public void setCheckmark(ResourceLocation research, boolean checked) {
		checkmarks.put(research,checked);
	}

	@Override
	public boolean isChecked(ResourceLocation research) {
		return checkmarks.getOrDefault(research,false);
	}

	@Override
	public Map<ResourceLocation, Boolean> getCheckmarks() {
		return checkmarks;
	}

	@Override
	public void writeToNBT(CompoundTag tag) {
		CompoundTag checkmarksTag = new CompoundTag();
		for (Map.Entry<ResourceLocation, Boolean> entry : checkmarks.entrySet()) {
			checkmarksTag.putBoolean(entry.getKey().toString(), entry.getValue());
		}
		tag.put("checkmarks", checkmarksTag);
	}

	@Override
	public void readFromNBT(CompoundTag tag) {
		CompoundTag checkmarksTag = tag.getCompound("checkmarks");
		checkmarks.clear();
		for (String key : checkmarksTag.getAllKeys()) {
			if (key.contains(":")) {
				checkmarks.put(new ResourceLocation(key), checkmarksTag.getBoolean(key));
			} else {
				checkmarks.put(new ResourceLocation(Embers.MODID, key), checkmarksTag.getBoolean(key));
			}
		}
	}
}
