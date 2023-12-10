package com.rekindled.embers.blockentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.rekindled.embers.blockentity.AutomaticHammerBlockEntity;

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

public class AutomaticHammerBlockEntityRenderer implements BlockEntityRenderer<AutomaticHammerBlockEntity> {

	public static BakedModel hammer;

	public AutomaticHammerBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {

	}

	@Override
	public void render(AutomaticHammerBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		BlockRenderDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRenderer();
		Level level = blockEntity.getLevel();
		BlockState blockState = level.getBlockState(blockEntity.getBlockPos());
		if (blockState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
			poseStack.pushPose();
			poseStack.translate(0.5D, 0.5D, 0.5D);
			Direction facing = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
			if (facing.getAxis() == Direction.Axis.Z)
				poseStack.mulPose(Axis.YP.rotationDegrees(facing.get2DDataValue() * 90 + 180));
			else
				poseStack.mulPose(Axis.YP.rotationDegrees(facing.get2DDataValue() * 90));
			poseStack.translate(0, 0, -0.25D);

			float hammerAngle = 0.0f;
			double processTicks = blockEntity.startTime + blockEntity.processTime - level.getGameTime() - partialTick;

			if (processTicks > blockEntity.processTime / 2.0f) {
				hammerAngle = (float) (-90.0f*(1 + 1.0 * Math.cos((processTicks * Math.PI) / blockEntity.processTime)));
			} else if (processTicks > 0) {
				hammerAngle = (float) (-90.0f*(1 - 1.0 * Math.cos((processTicks * Math.PI) / blockEntity.processTime)));
			}
			poseStack.mulPose(Axis.XP.rotationDegrees(hammerAngle));
			poseStack.translate(-0.5D, -0.5D, -0.5D);
			if (hammer != null)
				blockrendererdispatcher.getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(Sheets.solidBlockSheet()), blockState, hammer, 0.0f, 0.0f, 0.0f, packedLight, packedOverlay, ModelData.EMPTY, Sheets.solidBlockSheet());
			poseStack.popPose();
		}
	}
}
