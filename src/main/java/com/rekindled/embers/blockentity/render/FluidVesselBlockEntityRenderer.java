package com.rekindled.embers.blockentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.rekindled.embers.blockentity.FluidVesselBlockEntity;
import com.rekindled.embers.render.FluidCuboid;
import com.rekindled.embers.render.FluidRenderer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraftforge.fluids.FluidStack;

public class FluidVesselBlockEntityRenderer implements BlockEntityRenderer<FluidVesselBlockEntity> {

	FluidCuboid cube = new FluidCuboid(new Vector3f(4, 2, 4), new Vector3f(12, 15, 12), FluidCuboid.DEFAULT_FACES);

	public FluidVesselBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {

	}

	@Override
	public void render(FluidVesselBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		if (blockEntity != null) {
			//render fluid
			FluidStack fluidStack = blockEntity.getFluidStack();
			int capacity = blockEntity.getCapacity();
			if (!fluidStack.isEmpty() && capacity > 0) {
				float offset = blockEntity.renderOffset;
				if (offset > 1.2f || offset < -1.2f) {
					offset = offset - ((offset / 12f + 0.1f) * partialTick);
					blockEntity.renderOffset = offset;
				} else {
					blockEntity.renderOffset = 0;
				}
				FluidRenderer.renderScaledCuboid(poseStack, bufferSource, cube, fluidStack, offset, capacity, packedLight, false);
			} else {
				blockEntity.renderOffset = 0;
			}
		}
	}
}
