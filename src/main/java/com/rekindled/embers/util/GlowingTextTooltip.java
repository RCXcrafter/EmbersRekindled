package com.rekindled.embers.util;

import org.joml.Matrix4f;

import com.rekindled.embers.gui.GuiCodex;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public class GlowingTextTooltip implements TooltipComponent {

	public FormattedCharSequence normalText;
	public FormattedCharSequence glowingText;

	public GlowingTextTooltip(FormattedCharSequence normalText, FormattedCharSequence glowingText) {
		this.normalText = normalText;
		this.glowingText = glowingText;
	}

	public GlowingTextTooltip(FormattedCharSequence glowingText) {
		this(Component.empty().getVisualOrderText(), glowingText);
	}

	public static class GlowingTextClientTooltip implements ClientTooltipComponent {

		GlowingTextTooltip tooltip;

		public GlowingTextClientTooltip(GlowingTextTooltip tooltip) {
			this.tooltip = tooltip;
		}

		@Override
		public int getHeight() {
			return 10;
		}

		@Override
		public int getWidth(Font font) {
			return font.width(tooltip.normalText) + font.width(tooltip.glowingText);
		}

		@Override
		public void renderText(Font font, int mouseX, int mouseY, Matrix4f matrix, MultiBufferSource.BufferSource bufferSource) {
			font.drawInBatch(tooltip.normalText, (float)mouseX, (float)mouseY, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
		}

		@Override
		public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
			GuiCodex.drawTextGlowingAura(font, graphics, tooltip.glowingText, font.width(tooltip.normalText) + x, y);
		}
	}
}
