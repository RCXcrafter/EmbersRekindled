package com.rekindled.embers.gui;

import java.util.ArrayList;

import com.rekindled.embers.Embers;
import com.rekindled.embers.item.AlchemyHintItem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SlateScreen extends AbstractContainerScreen<SlateMenu> {

	ResourceLocation inventory = new ResourceLocation(Embers.MODID, "textures/gui/inventory_gui.png");
	ResourceLocation slate = new ResourceLocation(Embers.MODID, "textures/gui/codebreaking_slate_gui.png");

	public SlateScreen(SlateMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
		this.imageHeight = SlateMenu.invHeight + SlateMenu.slateHeight;
		this.imageWidth = SlateMenu.invWidth;
		this.inventoryLabelY = this.imageHeight - 94;
	}

	@Override
	public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
		this.renderBackground(graphics);
		super.render(graphics, pMouseX, pMouseY, pPartialTick);
		this.renderTooltip(graphics, pMouseX, pMouseY);
	}

	@Override
	public void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
		int invX = (this.width - this.imageWidth) / 2;
		int x = (this.width - SlateMenu.slateWidth) / 2;
		int y = (this.height - this.imageHeight) / 2;
		graphics.blit(inventory, invX, y + SlateMenu.slateHeight, 0, 0, SlateMenu.invWidth, SlateMenu.invHeight);
		graphics.blit(slate, x, y, 0, 0, SlateMenu.slateWidth, SlateMenu.slateHeight);

		//render ingredients
		ItemStack baseStack = this.menu.getSlot(0).getItem();
		ArrayList<ItemStack> inputs = AlchemyHintItem.getInputs(baseStack);
		for (int j = 0; j < inputs.size(); ++j) {
			int slotX = x + SlateMenu.slateMargin + j * SlateMenu.layerWidth / inputs.size() + SlateMenu.layerWidth / (2 * inputs.size()) - 12;
			graphics.blit(slate, slotX, y + SlateMenu.slateMargin, 192, 0, 24, 24);
			graphics.renderFakeItem(inputs.get(j), slotX + 4, y + SlateMenu.slateMargin + 4);
		}

		//render guesses
		for (int i = 0; i < SlateMenu.slotCount; ++i) {
			int height = y + SlateMenu.slateMargin + i * SlateMenu.layerHeight + SlateMenu.layerHeight / 2 + 13;

			graphics.blit(slate, x - 30, height, 192, 24, 26, 26);

			if (baseStack.isEmpty())
				continue;

			ItemStack stack = this.menu.getSlot(i).getItem();
			ArrayList<ItemStack> aspects = AlchemyHintItem.getAspects(stack);
			int blackPins = AlchemyHintItem.getBlackPins(stack);
			int whitePins = AlchemyHintItem.getWhitePins(stack);

			for (int j = 0; j < inputs.size(); ++j) {
				int slotX = x + SlateMenu.slateMargin + j * SlateMenu.layerWidth / inputs.size() + SlateMenu.layerWidth / (2 * inputs.size()) - 10;
				graphics.blit(slate, slotX, height + 2, 218, 20, 20, 24);
				if (j < aspects.size())
					graphics.renderFakeItem(aspects.get(j), slotX + 2, height + 5);
			}

			//render hint
			int hintX = x + 155;
			int hintY = height + 3;
			graphics.blit(slate, hintX, hintY, 216, 0, 19, 19);

			if (inputs.size() <= 4) { //4 pin slots
				for (int h = 0; h < 2; ++h) {
					for (int v = 0; v < 2; ++v) {
						if (blackPins > 0) {
							graphics.blit(slate, hintX + 3 + v * 8, hintY + 3 + h * 8, 198, 50, 6, 6);
							blackPins--;
						} else if (whitePins > 0) {
							graphics.blit(slate, hintX + 3 + v * 8, hintY + 3 + h * 8, 204, 50, 6, 6);
							whitePins--;
						} else {
							graphics.blit(slate, hintX + 3 + v * 8, hintY + 3 + h * 8, 192, 50, 6, 6);
						}
					}
				}
			} else { //9 pin slots
				for (int h = 0; h < 3; ++h) {
					for (int v = 0; v < 2; ++v) {
						if (blackPins > 0) {
							graphics.blit(slate, hintX + 1 + v * 6, hintY + 1 + h * 6, 198, 50, 6, 6);
							blackPins--;
						} else if (whitePins > 0) {
							graphics.blit(slate, hintX + 1 + v * 6, hintY + 1 + h * 6, 204, 50, 6, 6);
							whitePins--;
						} else {
							graphics.blit(slate, hintX + 1 + v * 6, hintY + 1 + h * 6, 192, 50, 6, 6);
						}
					}
				}
			}
		}
	}

	@Override
	public void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
		graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
	}
}
