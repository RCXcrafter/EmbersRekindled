package com.rekindled.embers.research.subtypes;

import java.util.Map;
import java.util.Set;

import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.gui.GuiCodex;
import com.rekindled.embers.research.ResearchBase;
import com.rekindled.embers.research.ResearchCategory;

import net.minecraft.world.item.ItemStack;

public class ResearchSwitchCategory extends ResearchBase {
	ResearchCategory targetCategory;
	int minEntries;

	public ResearchSwitchCategory(String location, ItemStack icon, double x, double y) {
		super(location, icon, x, y);
	}

	public ResearchSwitchCategory setTargetCategory(ResearchCategory category) {
		this.targetCategory = category;
		return this;
	}

	public ResearchSwitchCategory setMinEntries(int entries) {
		this.minEntries = entries;
		return this;
	}

	@Override
	public void getAllResearch(Set<ResearchBase> result) {
		if(result.contains(this))
			return;
		targetCategory.getAllResearch(result);
	}

	@Override
	public void findByTag(String match, Map<ResearchBase, Integer> result, Set<ResearchCategory> categories) {
		int startResults = result.size();
		targetCategory.findByTag(match,result,categories);
		if(startResults != result.size())
		{
			int categoryScore = result.entrySet().stream().filter(entry -> targetCategory.researches.contains(entry.getKey())).mapToInt(Map.Entry::getValue).max().orElse(0);
			result.put(this,categoryScore);
		}
	}

	@Override
	public boolean onOpen(GuiCodex gui) {
		gui.pushLastCategory(gui.researchCategory);
		gui.researchCategory = targetCategory;
		gui.playSound(EmbersSounds.CODEX_CATEGORY_SWITCH.get());
		return false;
	}

	@Override
	public void check(boolean checked) {
		for (ResearchBase research : targetCategory.researches) {
			research.check(checked);
		}
	}

	@Override
	public boolean isChecked() {
		for (ResearchBase research : targetCategory.researches) {
			if (!research.isChecked())
				return false;
		}
		return true;
	}

	@Override
	public boolean isHidden() {
		return targetCategory.researches.size() < minEntries;
	}
}
