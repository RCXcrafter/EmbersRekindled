package com.rekindled.embers.util;

import org.joml.Matrix4f;

import com.rekindled.embers.gui.GuiCodex;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public class GlowingTextTooltip implements TooltipComponent {

	public Component normalText;
	public Component glowingText;
	public float intensity = -2.0F;

	public GlowingTextTooltip(Component normalText, Component glowingText, float intensity) {
		this.normalText = normalText;
		this.glowingText = glowingText;
		this.intensity = intensity;
	}

	public GlowingTextTooltip(Component glowingText, float intensity) {
		this(Component.empty(), glowingText, intensity);
	}

	public GlowingTextTooltip(Component normalText, Component glowingText) {
		this(normalText, glowingText, -2.0F);
	}

	public GlowingTextTooltip(Component glowingText) {
		this(Component.empty(), glowingText, -2.0F);
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
			if (tooltip.intensity < -1.0f) {
				GuiCodex.drawTextGlowingAura(font, graphics, tooltip.glowingText.getVisualOrderText(), font.width(tooltip.normalText) + x, y);
			} else {
				font.drawInBatch(tooltip.glowingText, x, y, -1, true, graphics.pose().last().pose(), graphics.bufferSource(), Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
				graphics.pose().pushPose();
				graphics.pose().translate(0, 0, 0.06);
				GuiCodex.drawTextGlowingAura(font, graphics, tooltip.glowingText.plainCopy().getVisualOrderText(), font.width(tooltip.normalText) + x, y, tooltip.intensity);
				graphics.pose().popPose();
			}
		}
	}
}
