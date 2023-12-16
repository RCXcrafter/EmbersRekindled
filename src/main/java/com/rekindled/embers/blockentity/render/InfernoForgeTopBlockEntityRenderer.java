package com.rekindled.embers.blockentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.rekindled.embers.blockentity.InfernoForgeTopBlockEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.data.ModelData;

public class InfernoForgeTopBlockEntityRenderer implements BlockEntityRenderer<InfernoForgeTopBlockEntity> {

	public static BakedModel hatch;

	public InfernoForgeTopBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {

	}

	@Override
	public void render(InfernoForgeTopBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		BlockRenderDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRenderer();
		Level level = blockEntity.getLevel();
		BlockState blockState = level.getBlockState(blockEntity.getBlockPos());
		if (blockState.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
			poseStack.pushPose();
			poseStack.translate(0.5D, 0.5D, 0.5D);
			Direction.Axis axis = blockState.getValue(BlockStateProperties.HORIZONTAL_AXIS);
			if (axis == Direction.Axis.Z)
				poseStack.mulPose(Axis.YP.rotationDegrees(90));

			double openTime = 7.0;
			double openAmount = 0.0D;
			double openTicks = level.getGameTime() + ((double) partialTick) - blockEntity.lastToggle;

			if (openTicks <= 0) {
				if (!blockEntity.open)
					openAmount = 1.0D;
			} else if (openTicks <= openTime) {
				if (blockEntity.open)
					openAmount = Math.sin(openTicks * Math.PI / (openTime * 2.0));
				else
					openAmount = 1.0D - Math.sin(openTicks * Math.PI / (openTime * 2.0));
			} else if (blockEntity.open)
				openAmount = 1.0D;

			poseStack.translate(-0.5D, -0.5D, -0.5D);
			if (hatch != null) {
				poseStack.translate(0, 0, -openAmount * 0.4375);
				blockrendererdispatcher.getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(Sheets.solidBlockSheet()), blockState, hatch, 0.0f, 0.0f, 0.0f, packedLight, packedOverlay, ModelData.EMPTY, Sheets.solidBlockSheet());

				poseStack.translate(1.0D, 0, 1.0D + openAmount * 2.0 * 0.4375);
				poseStack.mulPose(Axis.YP.rotationDegrees(180));
				blockrendererdispatcher.getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(Sheets.solidBlockSheet()), blockState, hatch, 0.0f, 0.0f, 0.0f, packedLight, packedOverlay, ModelData.EMPTY, Sheets.solidBlockSheet());
			}
			poseStack.popPose();
		}
	}
}
