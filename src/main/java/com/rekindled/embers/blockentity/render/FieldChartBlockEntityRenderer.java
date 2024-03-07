package com.rekindled.embers.blockentity.render;

import java.awt.Color;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rekindled.embers.ConfigManager;
import com.rekindled.embers.EmbersClientEvents;
import com.rekindled.embers.block.FieldChartBlock;
import com.rekindled.embers.blockentity.FieldChartBlockEntity;
import com.rekindled.embers.render.EmbersRenderTypes;
import com.rekindled.embers.util.EmberGenUtil;
import com.rekindled.embers.util.Misc;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class FieldChartBlockEntityRenderer implements BlockEntityRenderer<FieldChartBlockEntity> {

	public static float baseHeight = 0.375f;
	public static float height = 0.55f;

	interface IChartSource {
		float get(int x, int z);
	}

	public FieldChartBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {

	}

	@Override
	public void render(FieldChartBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		if (blockEntity != null) {
			VertexConsumer buffer = bufferSource.getBuffer(ConfigManager.RENDER_FALLBACK.get() ? EmbersRenderTypes.FIELD_CHART_FALLBACK : EmbersRenderTypes.FIELD_CHART);
			RenderSystem.enableDepthTest();
			RenderSystem.disableCull();

			BlockState state = blockEntity.getLevel().getBlockState(blockEntity.getBlockPos());

			if (state.hasProperty(FieldChartBlock.INVERTED) && state.getValue(FieldChartBlock.INVERTED)) {
				renderChart(blockEntity.getLevel(), blockEntity.getBlockPos(), 0, 0, 0, buffer, poseStack.last().pose(), (cx, cz) -> EmberGenUtil.getEmberStability(EmbersClientEvents.seed, cx, cz), new Color(16,64,255), new Color(16,192,255), new Color(8,255,255));
			} else {
				renderChart(blockEntity.getLevel(), blockEntity.getBlockPos(), 0, 0, 0, buffer, poseStack.last().pose(), (cx, cz) -> EmberGenUtil.getEmberDensity(EmbersClientEvents.seed, cx, cz), new Color(255,64,16), new Color(255,192,16), new Color(255,255,8));
			}
			RenderSystem.enableCull();
		}
	}

	public void renderChart(Level level, BlockPos pos, float x, float y, float z, VertexConsumer buffer, Matrix4f matrix4f, IChartSource source, Color color1, Color color2, Color color3) {
		int signal = level.getBestNeighborSignal(pos);
		float brightness = 1.0f;
		if (signal > 2) {
			brightness = Misc.getLightBrightness(15 - signal, EmbersClientEvents.ticks);
		}
		float red1 = brightness * color1.getRed() / 255f;
		float green1 = brightness * color1.getGreen() / 255f;
		float blue1 = brightness * color1.getBlue() / 255f;
		for (float i = -160; i < 160; i += 32) {
			for (float j = -160; j < 160; j += 32) {
				float amountul = source.get(pos.getX() + (int) i / 2, pos.getZ() + (int) j / 2);
				float amountur = source.get(pos.getX() + (int) i / 2 + 16, pos.getZ() + (int) j / 2);
				float amountdr = source.get(pos.getX() + (int) i / 2 + 16, pos.getZ() + (int) j / 2 + 16);
				float amountdl = source.get(pos.getX() + (int) i / 2, pos.getZ() + (int) j / 2 + 16);
				float alphaul = Math.min(1.0f, Math.max(0.0f, 1.0f - Math.max(Math.abs(i), Math.abs(j)) / 160f));
				float alphaur = Math.min(1.0f, Math.max(0.0f, 1.0f - Math.max(Math.abs(i + 32f), Math.abs(j)) / 160f));
				float alphadr = Math.min(1.0f, Math.max(0.0f, 1.0f - Math.max(Math.abs(i + 32f), Math.abs(j + 32f)) / 160f));
				float alphadl = Math.min(1.0f, Math.max(0.0f, 1.0f - Math.max(Math.abs(i), Math.abs(j + 32f)) / 160f));
				buffer.vertex(matrix4f, x + 0.5f + 1.25f * (i / 160f), y + baseHeight + amountul * height, z + 0.5f + 1.25f * (j / 160f)).uv(0, 0).color(red1 * alphaul, green1 * alphaul, blue1 * alphaul, 1).endVertex();
				buffer.vertex(matrix4f, x + 0.5f + 1.25f * (i / 160f) + 0.25f, y + baseHeight + amountur * height, z + 0.5f + 1.25f * (j / 160f)).uv(1, 0).color(red1 * alphaur, green1 * alphaur, blue1 * alphaur, 1).endVertex();
				buffer.vertex(matrix4f, x + 0.5f + 1.25f * (i / 160f) + 0.25f, y + baseHeight + amountdr * height, z + 0.5f + 1.25f * (j / 160f) + 0.25f).uv(1, 1).color(red1 * alphadr, green1 * alphadr, blue1 * alphadr, 1).endVertex();
				buffer.vertex(matrix4f, x + 0.5f + 1.25f * (i / 160f), y + baseHeight + amountdl * height, z + 0.5f + 1.25f * (j / 160f) + 0.25f).uv(0, 1).color(red1 * alphadl, green1 * alphadl, blue1 * alphadl, 1).endVertex();
			}
		}
		float red2 = brightness * color2.getRed() / 255f;
		float green2 = brightness * color2.getGreen() / 255f;
		float blue2 = brightness * color2.getBlue() / 255f;
		for (float i = -160; i < 160; i += 32) {
			for (float j = -160; j < 160; j += 32) {
				float amountul = source.get(pos.getX() + (int) i / 2, pos.getZ() + (int) j / 2);
				float amountur = source.get(pos.getX() + (int) i / 2 + 16, pos.getZ() + (int) j / 2);
				float amountdr = source.get(pos.getX() + (int) i / 2 + 16, pos.getZ() + (int) j / 2 + 16);
				float amountdl = source.get(pos.getX() + (int) i / 2, pos.getZ() + (int) j / 2 + 16);
				float alphaul = Math.min(1.0f, Math.max(0.0f, 1.0f - Math.max(Math.abs(i), Math.abs(j)) / 160f) * amountul * amountul) * 0.875f;
				float alphaur = Math.min(1.0f, Math.max(0.0f, 1.0f - Math.max(Math.abs(i + 32f), Math.abs(j)) / 160f) * amountur * amountur) * 0.875f;
				float alphadr = Math.min(1.0f, Math.max(0.0f, 1.0f - Math.max(Math.abs(i + 32f), Math.abs(j + 32f)) / 160f) * amountdr * amountdr) * 0.875f;
				float alphadl = Math.min(1.0f, Math.max(0.0f, 1.0f - Math.max(Math.abs(i), Math.abs(j + 32f)) / 160f) * amountdl * amountdl) * 0.875f;
				buffer.vertex(matrix4f, x + 0.5f + 1.25f * (i / 160f), y + baseHeight + amountul * height, z + 0.5f + 1.25f * (j / 160f)).uv(0, 0).color(red2 * alphaul, green2 * alphaul, blue2 * alphaul, 1).endVertex();
				buffer.vertex(matrix4f, x + 0.5f + 1.25f * (i / 160f) + 0.25f, y + baseHeight + amountur * height, z + 0.5f + 1.25f * (j / 160f)).uv(1, 0).color(red2 * alphaur, green2 * alphaur, blue2 * alphaur, 1).endVertex();
				buffer.vertex(matrix4f, x + 0.5f + 1.25f * (i / 160f) + 0.25f, y + baseHeight + amountdr * height, z + 0.5f + 1.25f * (j / 160f) + 0.25f).uv(1, 1).color(red2 * alphadr, green2 * alphadr, blue2 * alphadr, 1).endVertex();
				buffer.vertex(matrix4f, x + 0.5f + 1.25f * (i / 160f), y + baseHeight + amountdl * height, z + 0.5f + 1.25f * (j / 160f) + 0.25f).uv(0, 1).color(red2 * alphadl, green2 * alphadl, blue2 * alphadl, 1).endVertex();
			}
		}
		float red3 = brightness * color3.getRed() / 255f;
		float green3 = brightness * color3.getGreen() / 255f;
		float blue3 = brightness * color3.getBlue() / 255f;
		for (float i = -160; i < 160; i += 32) {
			for (float j = -160; j < 160; j += 32) {
				float amountul = source.get(pos.getX() + (int) i / 2, pos.getZ() + (int) j / 2);
				float amountur = source.get(pos.getX() + (int) i / 2 + 16, pos.getZ() + (int) j / 2);
				float amountdr = source.get(pos.getX() + (int) i / 2 + 16, pos.getZ() + (int) j / 2 + 16);
				float amountdl = source.get(pos.getX() + (int) i / 2, pos.getZ() + (int) j / 2 + 16);
				float alphaul = Math.min(1.0f, Math.max(0.0f, 1.0f - Math.max(Math.abs(i), Math.abs(j)) / 160f) * amountul * amountul * amountul);
				float alphaur = Math.min(1.0f, Math.max(0.0f, 1.0f - Math.max(Math.abs(i + 32f), Math.abs(j)) / 160f) * amountur * amountur * amountur);
				float alphadr = Math.min(1.0f, Math.max(0.0f, 1.0f - Math.max(Math.abs(i + 32f), Math.abs(j + 32f)) / 160f) * amountdr * amountdr * amountdr);
				float alphadl = Math.min(1.0f, Math.max(0.0f, 1.0f - Math.max(Math.abs(i), Math.abs(j + 32f)) / 160f) * amountdl * amountdl * amountdl);
				buffer.vertex(matrix4f, x + 0.5f + 1.25f * (i / 160f), y + baseHeight + amountul * height, z + 0.5f + 1.25f * (j / 160f)).uv(0, 0).color(red3 * alphaul, green3 * alphaul, blue3 * alphaul, 1).endVertex();
				buffer.vertex(matrix4f, x + 0.5f + 1.25f * (i / 160f) + 0.25f, y + baseHeight + amountur * height, z + 0.5f + 1.25f * (j / 160f)).uv(1, 0).color(red3 * alphaur, green3 * alphaur, blue3 * alphaur, 1).endVertex();
				buffer.vertex(matrix4f, x + 0.5f + 1.25f * (i / 160f) + 0.25f, y + baseHeight + amountdr * height, z + 0.5f + 1.25f * (j / 160f) + 0.25f).uv(1, 1).color(red3 * alphadr, green3 * alphadr, blue3 * alphadr, 1).endVertex();
				buffer.vertex(matrix4f, x + 0.5f + 1.25f * (i / 160f), y + baseHeight + amountdl * height, z + 0.5f + 1.25f * (j / 160f) + 0.25f).uv(0, 1).color(red3 * alphadl, green3 * alphadl, blue3 * alphadl, 1).endVertex();
			}
		}
	}
}
