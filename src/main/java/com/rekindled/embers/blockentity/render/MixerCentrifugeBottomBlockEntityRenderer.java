package com.rekindled.embers.blockentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.rekindled.embers.blockentity.MixerCentrifugeBottomBlockEntity;
import com.rekindled.embers.blockentity.MixerCentrifugeBottomBlockEntity.MixerFluidTank;
import com.rekindled.embers.render.FluidCuboid;
import com.rekindled.embers.render.FluidRenderer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class MixerCentrifugeBottomBlockEntityRenderer implements BlockEntityRenderer<MixerCentrifugeBottomBlockEntity> {

	FluidCuboid cubeNorth = new FluidCuboid(new Vector3f(7, 12, 3), new Vector3f(9, 15, 4), FluidCuboid.DEFAULT_FACES);
	FluidCuboid cubeSouth = new FluidCuboid(new Vector3f(7, 12, 12), new Vector3f(9, 15, 13), FluidCuboid.DEFAULT_FACES);
	FluidCuboid cubeEast = new FluidCuboid(new Vector3f(12, 12, 7), new Vector3f(13, 15, 9), FluidCuboid.DEFAULT_FACES);
	FluidCuboid cubeWest = new FluidCuboid(new Vector3f(3, 12, 7), new Vector3f(4, 15, 9), FluidCuboid.DEFAULT_FACES);
	FluidCuboid[] cubes = new FluidCuboid[] {cubeNorth, cubeSouth, cubeEast, cubeWest};

	public MixerCentrifugeBottomBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {

	}

	@Override
	public void render(MixerCentrifugeBottomBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		if (blockEntity != null) {
			//render fluid
			for (int i = 0; i < blockEntity.getTanks().length; i++) {
				MixerFluidTank fluidStack = blockEntity.getTanks()[i];
				int capacity = fluidStack.getCapacity();
				if (!fluidStack.isEmpty() && capacity > 0) {
					float offset = fluidStack.renderOffset;
					if (offset > 1.2f || offset < -1.2f) {
						offset = offset - ((offset / 12f + 0.1f) * partialTick);
						fluidStack.renderOffset = offset;
					} else {
						fluidStack.renderOffset = 0;
					}
					FluidRenderer.renderScaledCuboid(poseStack, bufferSource, cubes[i], fluidStack.getFluid(), offset, capacity, packedLight, false);
				} else {
					fluidStack.renderOffset = 0;
				}
			}
		}
	}
}
