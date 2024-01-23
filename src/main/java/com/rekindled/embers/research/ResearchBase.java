package com.rekindled.embers.research;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rekindled.embers.ConfigManager;
import com.rekindled.embers.Embers;
import com.rekindled.embers.gui.GuiCodex;
import com.rekindled.embers.util.Vec2i;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ResearchBase {
	public String name = "";
	public double u = 0.0;
	public double v = 0.0;
	public ItemStack icon = ItemStack.EMPTY;
	public int x = 0;
	public int y = 0;
	public List<ResearchBase> ancestors = new ArrayList<>();
	public ResearchBase subCategory = null;
	public ResourceLocation iconBackground = ResearchManager.PAGE_ICONS;
	public ResourceLocation background = new ResourceLocation(Embers.MODID, "textures/gui/codex_normal.png");

	public ResearchBase firstPage;
	public int pageNumber;
	List<ResearchBase> pages = new ArrayList<>();

	public float selectedAmount = 0;
	public float selectionTarget = 0;

	public float shownAmount = 0;
	public float shownTarget = 0;

	public boolean checked;

	public ResearchBase(String location, ItemStack icon, double x, double y){
		this.name = location;
		this.icon = icon;
		this.x = 48+(int)(x*24);
		this.y = 48+(int)(y*24);
	}

	public ResearchBase(String location, ItemStack icon, Vec2i pos) {
		this(location, icon, pos.x, pos.y);
	}

	public List<ResearchCategory> getNeededFor() {
		ArrayList<ResearchCategory> neededFor = new ArrayList<>();
		if (ConfigManager.CODEX_PROGRESSION.get())
			for (ResearchCategory category : ResearchManager.researches) {
				if (category.prerequisites.contains(this))
					neededFor.add(category);
			}
		return neededFor;
	}

	public List<ResearchBase> getAllRequirements() {
		if (subCategory == null) {
			return ancestors;
		}
		List<ResearchBase> requirements = new ArrayList<>();
		requirements.addAll(ancestors);
		requirements.add(subCategory);
		return requirements;
	}

	public void findByTag(String match,Map<ResearchBase,Integer> result, Set<ResearchCategory> categories) {
		if (result.containsKey(this))
			return;
		String[] matchParts = match.split("\\|");
		int totalScore = 0;

		for (String matchPart : matchParts)
			if (!matchPart.isEmpty()) {
				int tagScore = matchTags(matchPart);
				int nameScore = scoreMatches(getName(), matchPart);
				int textScore = scoreMatches(getText().getString(), matchPart);
				int score = textScore + tagScore * 100 + nameScore * 1000;
				if (score <= 0)
					return;
				totalScore += score;
			}

		if (totalScore > 0)
			result.put(this, totalScore);
	}

	public int matchTags(String match) {
		int score = 0;
		int matches = 0;
		for (String tag : getTags()) {
			int matchScore = scoreMatches(tag,match);
			if (matchScore > 0)
				matches++;
			score += matchScore;
		}
		return score + matches * 100;
	}

	private int scoreMatches(String tag, String match) {
		tag = tag.toLowerCase();
		match = match.toLowerCase();
		int matches = 0;
		int positionalScore = 0;
		int index = 0;
		do {
			index = tag.indexOf(match, index);
			if (index >= 0) {
				matches++;
				positionalScore += tag.length() - index;
				index++;
			}
		} while (index >= 0);
		return matches * 10 + positionalScore;

	}

	public ResearchBase addAncestor(ResearchBase base){
		this.ancestors.add(base);
		return this;
	}

	public ResearchBase setIconBackground(ResourceLocation resourceLocation, double u, double v) {
		this.iconBackground = resourceLocation;
		this.u = u;
		this.v = v;
		return this;
	}

	public ResearchBase setBackground(ResourceLocation resourceLocation) {
		this.background = resourceLocation;
		return this;
	}

	public ResearchBase addPage(ResearchBase page) {
		if (firstPage != null)
			return firstPage.addPage(page);
		pages.add(page);
		page.pageNumber = getPageCount();
		page.firstPage = getFirstPage();
		return this;
	}

	public boolean isHidden() {
		return false;
	}

	public void check(boolean checked) {
		this.checked = checked;
	}

	public boolean isChecked() {
		return checked;
	}

	public boolean areAncestorsChecked() {
		if (ConfigManager.CODEX_PROGRESSION.get())
			return isChecked() || ancestors.stream().allMatch(ResearchBase::isChecked)/*ancestors.isEmpty() || ancestors.stream().anyMatch(ResearchBase::isChecked)*/;
		else
			return true;
	}

	@OnlyIn(Dist.CLIENT)
	public List<Component> getTooltip(boolean showTooltips) {
		ArrayList<Component> tooltip = new ArrayList<>();
		if (showTooltips || !isChecked()) {
			for (ResearchCategory neededFor : getNeededFor()) {
				tooltip.add(Component.translatable(Embers.MODID + ".research.prerequisite", neededFor.getName()));
			}
		}
		return tooltip;
	}

	@OnlyIn(Dist.CLIENT)
	public String getName() {
		return I18n.get(Embers.MODID + ".research.page."+name);
	}

	@OnlyIn(Dist.CLIENT)
	public String getTitle() {
		if (hasMultiplePages())
			return I18n.get(Embers.MODID + ".research.multipage", I18n.get(Embers.MODID + ".research.page."+getFirstPage().name+".title"), pageNumber+1, getPageCount()+1);
		else
			return I18n.get(Embers.MODID + ".research.page."+name+".title");
	}

	@OnlyIn(Dist.CLIENT)
	private String[] getTags() {
		String translateKey = Embers.MODID + ".research.page." + name + ".tags";
		if (I18n.exists(translateKey)) {
			return I18n.get(translateKey).split(";");
		} else {
			return new String[0];
		}
	}

	@OnlyIn(Dist.CLIENT)
	public Component getText() {
		return Component.translatable(Embers.MODID + ".research.page."+name+".desc");
	}

	@OnlyIn(Dist.CLIENT)
	public List<FormattedCharSequence> getLines(Font fontRenderer, FormattedText s, int width) {
		return fontRenderer.split(s, width);
	}

	public ResourceLocation getBackground() {
		return background;
	}

	public ResourceLocation getIconBackground() {
		return iconBackground;
	}

	public double getIconBackgroundU() {
		return u;
	}

	public double getIconBackgroundV() {
		return v;
	}

	public ItemStack getIcon() {
		return icon;
	}

	public boolean hasMultiplePages() {
		return getPageCount() > 0;
	}

	public ResearchBase getPage(int i) {
		i = Mth.clamp(i,0,getPageCount());
		if (i <= 0)
			return getFirstPage();
		else
			return getPages().get(i-1);
	}

	public ResearchBase getFirstPage() {
		if(firstPage != null)
			return firstPage;
		else
			return this;
	}

	public ResearchBase getNextPage() {
		return getPage(pageNumber+1);
	}

	public ResearchBase getPreviousPage() {
		return getPage(pageNumber-1);
	}

	public int getPageCount() {
		return getPages().size();
	}

	public List<ResearchBase> getPages() {
		if (firstPage != null)
			return firstPage.pages;
		else
			return pages;
	}

	public boolean onOpen(GuiCodex gui) {
		return true;
	}

	public boolean onClose(GuiCodex gui) {
		return true;
	}

	public void renderPageContent(GuiGraphics graphics, GuiCodex gui, int basePosX, int basePosY, Font fontRenderer) {
		List<FormattedCharSequence> strings = getLines(fontRenderer, getText(), 152);
		for (int i = 0; i < Math.min(strings.size(),17); i++){
			GuiCodex.drawTextGlowing(fontRenderer, graphics, strings.get(i), basePosX+20, basePosY+43+i*(fontRenderer.lineHeight+3));
		}
	}

	public void getAllResearch(Set<ResearchBase> result) {
		if (result.contains(this))
			return;
		result.add(this);
	}

	public boolean isPathTowards(ResearchBase target) {
		return this == target;
	}
}
