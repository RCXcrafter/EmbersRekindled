package com.rekindled.embers.blockentity.render;

import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rekindled.embers.blockentity.FluidTransferBlockEntity;
import com.rekindled.embers.render.FluidCuboid;
import com.rekindled.embers.render.FluidRenderer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class FluidTransferBlockEntityRenderer implements BlockEntityRenderer<FluidTransferBlockEntity> {

	FluidCuboid cube = new FluidCuboid(new Vector3f(4, 4, 4), new Vector3f(12, 12, 12), FluidCuboid.DEFAULT_FACES);

	public FluidTransferBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {

	}

	@Override
	public void render(FluidTransferBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		if (blockEntity != null) {
			//render fluid
			if (!blockEntity.filterFluid.isEmpty()) {
				FluidRenderer.renderScaledCuboid(poseStack, bufferSource, cube, blockEntity.filterFluid, 0, blockEntity.filterFluid.getAmount(), packedLight, false);
			}
		}
	}
}
