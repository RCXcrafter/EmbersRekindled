package com.rekindled.embers.blockentity.render;

import java.util.Random;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.rekindled.embers.Embers;
import com.rekindled.embers.blockentity.CrystalCellBlockEntity;
import com.rekindled.embers.render.EmbersRenderTypes;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class CrystalCellBlockEntityRenderer implements BlockEntityRenderer<CrystalCellBlockEntity> {

	public ResourceLocation texture = new ResourceLocation(Embers.MODID + ":textures/block/crystal_material.png");
	Random random = new Random();

	public CrystalCellBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {

	}

	@Override
	public void render(CrystalCellBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		random.setSeed(blockEntity.seed);
		float capacityFactor = 120000.0f;
		double emberCapacity = blockEntity.renderCapacity;
		double lerpCapacity = emberCapacity * partialTick + blockEntity.renderCapacityLast * (1-partialTick);
		int numLayers = 2 + (int) Math.floor(lerpCapacity / capacityFactor) + 1;
		int numLayersOld = numLayers - 1;
		float growthFactor = getGrowthFactor(capacityFactor, lerpCapacity);
		float layerHeight = 0.25f;
		float height = layerHeight * numLayers * growthFactor + numLayersOld * layerHeight * (1-growthFactor);
		float[] widths = new float[numLayers + 1];
		float[] oldWidths = new float[numLayers + 1];
		for (float i = 0; i < numLayers + 1; i++) {
			float rand = random.nextFloat();
			if (i < numLayers / 2.0f) {
				widths[(int) i] = (i / (numLayers / 2.0f)) * (layerHeight * 0.1875f + layerHeight * 0.09375f * rand) * numLayers;
			} else {
				widths[(int) i] = ((numLayers - i) / (numLayers / 2.0f)) * (layerHeight * 0.1875f + layerHeight * 0.09375f * rand) * numLayers;
			}
			if (i >= numLayersOld)
				continue;
			if (i < numLayersOld / 2.0) {
				oldWidths[(int) i] = (i / (numLayersOld / 2.0f)) * (layerHeight * 0.1875f + layerHeight * 0.09375f * rand) * numLayersOld;
			} else {
				oldWidths[(int) i] = ((numLayersOld - i) / (numLayersOld / 2.0f)) * (layerHeight * 0.1875f + layerHeight * 0.09375f * rand) * numLayersOld;
			}
		}

		RenderSystem.setShaderTexture(0, texture);
		RenderSystem.disableCull();
		//RenderSystem.enableTexture();
		VertexConsumer buffer = bufferSource.getBuffer(EmbersRenderTypes.CRYSTAL);

		for (float j = 0; j < 12; j++) {

			poseStack.pushPose();

			float scale = j / 12.0f;

			poseStack.translate(0.5, height / 2.0f + 1.5, 0.5);
			poseStack.scale(scale, scale, scale);


			poseStack.mulPose(Axis.YP.rotationDegrees(partialTick + blockEntity.ticksExisted % 360));
			poseStack.mulPose(Axis.XP.rotationDegrees(30.0f * (float) Math.sin(Math.toRadians((partialTick / 3.0f) + (blockEntity.ticksExisted / 3.0f) % 360))));

			Matrix4f matrix4f = poseStack.last().pose();
			for (int i = 0; i < widths.length - 1; i++) {
				float width = widths[i] * growthFactor + oldWidths[i] * (1-growthFactor);
				float nextWidth = widths[i + 1] * growthFactor + oldWidths[i + 1] * (1-growthFactor);
				buffer.vertex(matrix4f, -width, layerHeight * i - height / 2.0f, -width).uv(0, 0).color(1, 1, 1, 0.65f).endVertex();
				buffer.vertex(matrix4f, width, layerHeight * i - height / 2.0f, -width).uv(0.5f, 0).color(1, 1, 1, 0.65f).endVertex();
				buffer.vertex(matrix4f, nextWidth, layerHeight + layerHeight * i - height / 2.0f, -nextWidth).uv(0.5f, 0.5f).color(1, 1, 1, 0.65f).endVertex();
				buffer.vertex(matrix4f, -nextWidth, layerHeight + layerHeight * i - height / 2.0f, -nextWidth).uv(0, 0.5f).color(1, 1, 1, 0.65f).endVertex();

				buffer.vertex(matrix4f, -width, layerHeight * i - height / 2.0f, width).uv(0, 0).color(1, 1, 1, 0.65f).endVertex();
				buffer.vertex(matrix4f, width, layerHeight * i - height / 2.0f, width).uv(0.5f, 0).color(1, 1, 1, 0.65f).endVertex();
				buffer.vertex(matrix4f, nextWidth, layerHeight + layerHeight * i - height / 2.0f, nextWidth).uv(0.5f, 0.5f).color(1, 1, 1, 0.65f).endVertex();
				buffer.vertex(matrix4f, -nextWidth, layerHeight + layerHeight * i - height / 2.0f, nextWidth).uv(0, 0.5f).color(1, 1, 1, 0.65f).endVertex();

				buffer.vertex(matrix4f, -width, layerHeight * i - height / 2.0f, -width).uv(0, 0).color(1, 1, 1, 0.65f).endVertex();
				buffer.vertex(matrix4f, -width, layerHeight * i - height / 2.0f, width).uv(0.5f, 0).color(1, 1, 1, 0.65f).endVertex();
				buffer.vertex(matrix4f, -nextWidth, layerHeight + layerHeight * i - height / 2.0f, nextWidth).uv(0.5f, 0.5f).color(1, 1, 1, 0.65f).endVertex();
				buffer.vertex(matrix4f, -nextWidth, layerHeight + layerHeight * i - height / 2.0f, -nextWidth).uv(0, 0.5f).color(1, 1, 1, 0.65f).endVertex();

				buffer.vertex(matrix4f, width, layerHeight * i - height / 2.0f, -width).uv(0, 0).color(1, 1, 1, 0.65f).endVertex();
				buffer.vertex(matrix4f, width, layerHeight * i - height / 2.0f, width).uv(0.5f, 0).color(1, 1, 1, 0.65f).endVertex();
				buffer.vertex(matrix4f, nextWidth, layerHeight + layerHeight * i - height / 2.0f, nextWidth).uv(0.5f, 0.5f).color(1, 1, 1, 0.65f).endVertex();
				buffer.vertex(matrix4f, nextWidth, layerHeight + layerHeight * i - height / 2.0f, -nextWidth).uv(0, 0.5f).color(1, 1, 1, 0.65f).endVertex();
			}
			poseStack.popPose();
		}

		RenderSystem.enableCull();
	}

	private float getGrowthFactor(float capacityFactor, double emberCapacity) {
		return (float) (emberCapacity % capacityFactor) / capacityFactor;
	}
}
