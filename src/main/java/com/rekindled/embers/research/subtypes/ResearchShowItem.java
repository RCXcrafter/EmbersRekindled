package com.rekindled.embers.research.subtypes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.rekindled.embers.Embers;
import com.rekindled.embers.gui.GuiCodex;
import com.rekindled.embers.research.ResearchBase;
import com.rekindled.embers.util.Vec2i;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

public class ResearchShowItem extends ResearchBase {
	LinkedList<DisplayItem> displayItems = new LinkedList<>();

	public ResearchShowItem(String location, ItemStack icon, double x, double y) {
		super(location, icon, x, y);
	}

	public ResearchShowItem(String location, ItemStack icon, Vec2i pos) {
		this(location,icon,pos.x,pos.y);
	}

	public ResearchShowItem addItem(DisplayItem item) {
		displayItems.add(item);
		return this;
	}

	@Override
	public List<FormattedCharSequence> getLines(Font renderer, FormattedText s, int width) {
		List<FormattedCharSequence> lines = new ArrayList<FormattedCharSequence>();
		int offset = displayItems.size() * 24;
		for(int i = 0; i < offset; i += renderer.lineHeight + 3)
			lines.add(0, FormattedCharSequence.EMPTY);
		lines.addAll(super.getLines(renderer, s, width));
		return lines;
	}

	@Override
	public void renderPageContent(PoseStack poseStack, GuiCodex gui, int basePosX, int basePosY, Font fontRenderer) {
		RenderSystem.setShaderTexture(0, getBackground());
		int textOffX = 3;
		int textOffY = 2;
		int y = 0;
		for (DisplayItem displayItem : displayItems) {
			ItemStack[] stacks = displayItem.getStacks();
			int batchOff = displayItem.sideText == null ? (152-stacks.length*24)/2 : 0;
			int slotY = basePosY - textOffY + 43 + y * 24;
			for(int x = 0; x < stacks.length; x++) {
				int slotX = basePosX - textOffX + batchOff + 20;
				gui.blit(poseStack, slotX + x * 24, slotY, 192, 0, 24, 24);
			}
			y++;
		}
		super.renderPageContent(poseStack, gui, basePosX, basePosY, fontRenderer);
		y = 0;
		for (DisplayItem displayItem : displayItems) {
			ItemStack[] stacks = displayItem.getStacks();
			int batchOff = displayItem.sideText == null ? (152-stacks.length*24)/2 : 0;
			int slotY = basePosY - textOffY + 43 + y * 24;
			for(int x = 0; x < stacks.length; x++) {
				int slotX = basePosX - textOffX + batchOff + 20 + x * 24;
				gui.renderItemStackMinusTooltipAt(stacks[x], slotX + 4, slotY + 4);
			}
			if(displayItem.sideText != null) {
				List<FormattedCharSequence> strings = fontRenderer.split(displayItem.getSideText(), 152 - stacks.length * 24);
				int textOff = strings.size() <= 1 ? (fontRenderer.lineHeight + 3) / 2 : 0;
				for (int i = 0; i < Math.min(strings.size(), 2); i++)
					GuiCodex.drawTextGlowing(fontRenderer, poseStack, strings.get(i), basePosX + 20 + stacks.length * 24, slotY + textOffY + textOff + i * (fontRenderer.lineHeight + 3));
			}
			y++;
		}
	}

	public static class DisplayItem {
		public ItemStack[] stacks;
		public String sideText;

		public DisplayItem(ItemStack... stacks) {
			this.stacks = stacks;
		}

		public DisplayItem(String sideText, ItemStack... stacks) {
			this.stacks = stacks;
			this.sideText = sideText;
		}

		public ItemStack[] getStacks() {
			return stacks;
		}

		public Component getSideText() {
			return Component.translatable(Embers.MODID + ".research.image." + sideText);
		}
	}
}
