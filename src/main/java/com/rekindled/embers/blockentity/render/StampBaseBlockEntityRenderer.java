package com.rekindled.embers.blockentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.rekindled.embers.blockentity.StampBaseBlockEntity;
import com.rekindled.embers.render.FluidCuboid;
import com.rekindled.embers.render.FluidRenderer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class StampBaseBlockEntityRenderer implements BlockEntityRenderer<StampBaseBlockEntity> {

	private final ItemRenderer itemRenderer;

	FluidCuboid cube = new FluidCuboid(new Vector3f(4, 12, 4), new Vector3f(12, 15, 12), FluidCuboid.DEFAULT_FACES);

	public StampBaseBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {
		this.itemRenderer = pContext.getItemRenderer();
	}

	@Override
	public void render(StampBaseBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		if (blockEntity != null) {
			//render item
			poseStack.pushPose();
			ItemStack stack = blockEntity.inventory.getStackInSlot(0);
			int seed = stack.isEmpty() ? 187 : Item.getId(stack.getItem()) + stack.getDamageValue();
			BakedModel bakedmodel = this.itemRenderer.getModel(stack, blockEntity.getLevel(), null, seed);
			float f2 = bakedmodel.getTransforms().getTransform(ItemTransforms.TransformType.GROUND).scale.y();
			poseStack.translate(0.5D, (double)(0.25F * f2) + 0.75D, 0.5D);
			this.itemRenderer.render(stack, ItemTransforms.TransformType.GROUND, false, poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY, bakedmodel);
			poseStack.popPose();

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
