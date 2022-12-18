package com.rekindled.embers.blockentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.block.EmberBoreBlock;
import com.rekindled.embers.blockentity.EmberBoreBlockEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.data.ModelData;

public class EmberBoreBlockEntityRenderer implements BlockEntityRenderer<EmberBoreBlockEntity> {

	public EmberBoreBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {

	}

	@Override
	public void render(EmberBoreBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		BlockRenderDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRenderer();
		float angle = blockEntity.angle;
		float lastAngle = blockEntity.lastAngle;

		BlockState bladeState = blockEntity.getLevel().getBlockState(blockEntity.getBlockPos());
		if (bladeState.getBlock() == RegistryManager.EMBER_BORE.get()) {
			bladeState = bladeState.setValue(EmberBoreBlock.BLADES, true);
			poseStack.pushPose();
			poseStack.translate(0.5D, -0.5D, 0.5D);
			if (bladeState.getValue(BlockStateProperties.HORIZONTAL_AXIS) == Axis.Z) {
				poseStack.mulPose(Vector3f.ZP.rotationDegrees(partialTick * angle + (1 - partialTick) * lastAngle));
			} else {
				poseStack.mulPose(Vector3f.XP.rotationDegrees(partialTick * angle + (1 - partialTick) * lastAngle));
			}
			poseStack.translate(-0.5D, -0.5D, -0.5D);
			blockrendererdispatcher.getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(Sheets.solidBlockSheet()), bladeState, blockrendererdispatcher.getBlockModel(bladeState), 0.0f, 0.0f, 0.0f, packedLight, packedOverlay, ModelData.EMPTY, Sheets.solidBlockSheet());
			poseStack.popPose();
		}
	}
}
