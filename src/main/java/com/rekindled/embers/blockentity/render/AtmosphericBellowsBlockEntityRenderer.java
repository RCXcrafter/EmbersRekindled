package com.rekindled.embers.blockentity.render;

import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rekindled.embers.EmbersClientEvents;
import com.rekindled.embers.blockentity.AtmosphericBellowsBlockEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public class AtmosphericBellowsBlockEntityRenderer implements BlockEntityRenderer<AtmosphericBellowsBlockEntity> {

	public static float length = 120;
	public static float blowLength = length / 3;
	public static float suckLength = length - blowLength;

	public static BakedModel top;
	public static BakedModel leather;
	public static Random random = new Random();

	public AtmosphericBellowsBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {
	}

	@Override
	public void render(AtmosphericBellowsBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		BlockRenderDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRenderer();
		BlockState blockState = blockEntity.getLevel().getBlockState(blockEntity.getBlockPos());
		random.setSeed(blockEntity.getBlockPos().asLong());
		float ticks = (EmbersClientEvents.ticks + partialTick + random.nextFloat(length)) % length;
		double magnitude = 1.0D;

		if (ticks < blowLength) {
			magnitude = ticks / blowLength;
		} else {
			magnitude = 1.0D - (ticks - blowLength) / suckLength;
		}

		poseStack.pushPose();
		poseStack.translate(0, magnitude * -0.1875D, 0);
		if (leather != null)
			blockrendererdispatcher.getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(Sheets.solidBlockSheet()), blockState, leather, 0.0f, 0.0f, 0.0f, packedLight, packedOverlay, ModelData.EMPTY, Sheets.solidBlockSheet());

		poseStack.translate(0, magnitude * -0.1875D, 0);
		if (top != null)
			blockrendererdispatcher.getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(Sheets.solidBlockSheet()), blockState, top, 0.0f, 0.0f, 0.0f, packedLight, packedOverlay, ModelData.EMPTY, Sheets.solidBlockSheet());

		poseStack.popPose();
	}
}
