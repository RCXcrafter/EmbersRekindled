package com.rekindled.embers.util;

import org.joml.Matrix4f;

import com.rekindled.embers.EmbersClientEvents;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public class HeatBarTooltip implements TooltipComponent {

	public FormattedCharSequence normalText;
	public float heat;
	public float maxHeat;
	public int barWidth;

	public HeatBarTooltip(FormattedCharSequence normalText, float heat, float maxHeat, int barWidth) {
		this.normalText = normalText;
		this.heat = heat;
		this.maxHeat = maxHeat;
		this.barWidth = barWidth;
	}

	public HeatBarTooltip(FormattedCharSequence normalText, float heat, float maxHeat) {
		this(normalText, heat, maxHeat, 96);
	}

	public static class HeatBarClientTooltip implements ClientTooltipComponent {

		HeatBarTooltip tooltip;

		public HeatBarClientTooltip(HeatBarTooltip tooltip) {
			this.tooltip = tooltip;
		}

		@Override
		public int getHeight() {
			return 10;
		}

		@Override
		public int getWidth(Font font) {
			return font.width(tooltip.normalText) + tooltip.barWidth - 24;
		}

		@Override
		public void renderText(Font font, int mouseX, int mouseY, Matrix4f matrix, MultiBufferSource.BufferSource bufferSource) {
			font.drawInBatch(tooltip.normalText, (float)mouseX, (float)mouseY, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
		}

		@Override
		public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
			int offset = font.width(tooltip.normalText) + 2;

			float x1 = x + offset + 3;
			float x2 = x + tooltip.barWidth - 3;
			x2 = x1 + (x2 - x1) * (tooltip.heat / tooltip.maxHeat);
			for (float j = 0; j < 10; j++) {
				float coeff = j / 10.0f;
				float coeff2 = (j + 1.0f) / 10.0f;
				for (float k = 0; k < 4; k += 0.5f) {
					float thick = (float) (k / 4.0f) * (tooltip.heat >= tooltip.maxHeat ? (float) Math.sin(EmbersClientEvents.ticks * 0.5f) * 2 + 3 : 1);
					RenderUtil.drawColorRectBatched(graphics.pose(), graphics.bufferSource(), x1 * (1.0f - coeff) + x2 * (coeff), y + k, 0, ((x2 - x1) / 10.0f), 8.0f - 2.0f * k,
							1.0f, 0.25f, 0.0625f, Math.min(1.0f, thick * 0.25f + thick * EmberGenUtil.getEmberDensity(6, (int) (EmbersClientEvents.ticks * 12 + 4 * (x1 * (1.0f - coeff) + x2 * (coeff))), 4 * (int) (y + k))),
							1.0f, 0.25f, 0.0625f, Math.min(1.0f, thick * 0.25f + thick * EmberGenUtil.getEmberDensity(6, (int) (EmbersClientEvents.ticks * 12 + 4 * (x1 * (1.0f - coeff2) + x2 * (coeff2))), 4 * (int) (y + k))),
							1.0f, 0.25f, 0.0625f, Math.min(1.0f, thick * 0.25f + thick * EmberGenUtil.getEmberDensity(6, (int) (EmbersClientEvents.ticks * 12 + 4 * (x1 * (1.0f - coeff2) + x2 * (coeff2))), 4 * (int) (y + (8.0 - k)))),
							1.0f, 0.25f, 0.0625f, Math.min(1.0f, thick * 0.25f + thick * EmberGenUtil.getEmberDensity(6, (int) (EmbersClientEvents.ticks * 12 + 4 * (x1 * (1.0f - coeff) + x2 * (coeff))), 4 * (int) (y + (8.0 - k)))));
				}
			}
			x1 = x + offset + 3;
			x2 = x + tooltip.barWidth - 3;
			float point = x1 + (x2 - x1) * (tooltip.heat / tooltip.maxHeat);

			for (float k = 0; k < 4; k += 0.5) {
				float thick = (float) (k / 4.0);
				RenderUtil.drawColorRectBatched(graphics.pose(), graphics.bufferSource(), point, y + k, 0, Math.min((x2 - point), ((x2 - x1) / 10.0f)), 8.0f - 2.0f * k,
						1.0f, 0.25f, 0.0625f, 1.0f * Math.min(1.0f, thick * 0.25f + thick * EmberGenUtil.getEmberDensity(6, (int) (EmbersClientEvents.ticks * 12 + 4 * (point)), 4 * (int) (y + k))),
						0.25f, 0.0625f, 0.015625f, 0.0f,
						0.25f, 0.0625f, 0.015625f, 0.0f,
						1.0f, 0.25f, 0.0625f, 1.0f * Math.min(1.0f, thick * 0.25f + thick * EmberGenUtil.getEmberDensity(6, (int) (EmbersClientEvents.ticks * 12 + 4 * (point)), 4 * (int) (y + (8.0 - k)))));
			}
			x1 = x + offset + 3;
			x2 = x + tooltip.barWidth - 3;
			x1 = x2 - (x2 - x1) * (1.0f - (tooltip.heat / tooltip.maxHeat));
			for (float j = 0; j < 10; j++) {
				float coeff = j / 10.0f;
				float coeff2 = (j + 1.0f) / 10.0f;
				for (float k = 0; k < 4; k += 0.5f) {
					float thick = (float) (k / 4.0);
					RenderUtil.drawColorRectBatched(graphics.pose(), graphics.bufferSource(), x1 * (1.0f - coeff) + x2 * (coeff), y + k, 0, ((x2 - x1) / 10.0f), 8.0f - 2.0f * k,
							0.25f, 0.0625f, 0.015625f, 0.75f * Math.min(1.0f, thick * 0.25f + thick * EmberGenUtil.getEmberDensity(6, (int) (EmbersClientEvents.ticks * 12 + 4 * (x1 * (1.0f - coeff) + x2 * (coeff))), 4 * (int) (y + k))),
							0.25f, 0.0625f, 0.015625f, 0.75f * Math.min(1.0f, thick * 0.25f + thick * EmberGenUtil.getEmberDensity(6, (int) (EmbersClientEvents.ticks * 12 + 4 * (x1 * (1.0f - coeff2) + x2 * (coeff2))), 4 * (int) (y + k))),
							0.25f, 0.0625f, 0.015625f, 0.75f * Math.min(1.0f, thick * 0.25f + thick * EmberGenUtil.getEmberDensity(6, (int) (EmbersClientEvents.ticks * 12 + 4 * (x1 * (1.0f - coeff2) + x2 * (coeff2))), 4 * (int) (y + (8.0 - k)))),
							0.25f, 0.0625f, 0.015625f, 0.75f * Math.min(1.0f, thick * 0.25f + thick * EmberGenUtil.getEmberDensity(6, (int) (EmbersClientEvents.ticks * 12 + 4 * (x1 * (1.0f - coeff) + x2 * (coeff))), 4 * (int) (y + (8.0 - k)))));
				}
			}
			RenderUtil.drawHeatBarEnd(graphics.pose(), graphics.bufferSource(), offset + x, y - 1, 0, 8, 10, 0, 0, 0.5f, 0.625f);
			RenderUtil.drawHeatBarEnd(graphics.pose(), graphics.bufferSource(), offset + x + tooltip.barWidth - 8 - 26, y - 1, 0, 8, 10, 0.5f, 0, 1.0f, 0.625f);
		}
	}
}
