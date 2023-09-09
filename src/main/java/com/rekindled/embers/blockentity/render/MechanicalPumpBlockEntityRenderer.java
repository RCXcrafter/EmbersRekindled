package com.rekindled.embers.blockentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.block.MechanicalPumpBlock;
import com.rekindled.embers.blockentity.MechanicalPumpBottomBlockEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.client.model.data.ModelData;

public class MechanicalPumpBlockEntityRenderer implements BlockEntityRenderer<MechanicalPumpBottomBlockEntity> {

	public MechanicalPumpBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {

	}

	@Override
	public void render(MechanicalPumpBottomBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		BlockRenderDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRenderer();
		BlockPos pos = blockEntity.getBlockPos().below();
		Level level = blockEntity.getLevel();

		double progress = blockEntity.totalProgress * partialTick + blockEntity.lastProgress * (1-partialTick);
		double amountUp = Math.abs(Math.sin((Math.PI * progress)/400.0));

		if (level.getBlockState(pos).isAir())
			packedLight = LightTexture.pack(level.getBrightness(LightLayer.BLOCK, pos), level.getBrightness(LightLayer.SKY, pos));

		BlockState bladeState = level.getBlockState(blockEntity.getBlockPos());
		if (bladeState.getBlock() == RegistryManager.MECHANICAL_PUMP.get()) {
			bladeState = bladeState.setValue(MechanicalPumpBlock.PISTON, true);
			poseStack.pushPose();
			poseStack.translate(0.0D, 1.0D + amountUp * 0.25D, 0.0D);

			blockrendererdispatcher.getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(Sheets.solidBlockSheet()), bladeState, blockrendererdispatcher.getBlockModel(bladeState), 0.0f, 0.0f, 0.0f, packedLight, packedOverlay, ModelData.EMPTY, Sheets.solidBlockSheet());

			bladeState = bladeState.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER);

			poseStack.translate(0.0D, amountUp * 0.25D, 0.0D);
			blockrendererdispatcher.getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(Sheets.solidBlockSheet()), bladeState, blockrendererdispatcher.getBlockModel(bladeState), 0.0f, 0.0f, 0.0f, packedLight, packedOverlay, ModelData.EMPTY, Sheets.solidBlockSheet());
			poseStack.popPose();
		}
	}
}
