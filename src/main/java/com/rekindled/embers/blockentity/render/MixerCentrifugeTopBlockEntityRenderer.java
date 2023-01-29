package com.rekindled.embers.blockentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.rekindled.embers.blockentity.MixerCentrifugeTopBlockEntity;
import com.rekindled.embers.render.FluidCuboid;
import com.rekindled.embers.render.FluidRenderer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraftforge.fluids.FluidStack;

public class MixerCentrifugeTopBlockEntityRenderer implements BlockEntityRenderer<MixerCentrifugeTopBlockEntity> {

	FluidCuboid cube = new FluidCuboid(new Vector3f(3, 2, 3), new Vector3f(13, 5, 13), FluidCuboid.DEFAULT_FACES);

	public MixerCentrifugeTopBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {

	}

	@Override
	public void render(MixerCentrifugeTopBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
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
