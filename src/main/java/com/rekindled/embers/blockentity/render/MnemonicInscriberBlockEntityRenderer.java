package com.rekindled.embers.blockentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.rekindled.embers.blockentity.MnemonicInscriberBlockEntity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class MnemonicInscriberBlockEntityRenderer implements BlockEntityRenderer<MnemonicInscriberBlockEntity> {

	private final ItemRenderer itemRenderer;

	public MnemonicInscriberBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {
		this.itemRenderer = pContext.getItemRenderer();
	}

	@Override
	public void render(MnemonicInscriberBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		BlockState blockState = blockEntity.getLevel().getBlockState(blockEntity.getBlockPos());
		if (blockState.hasProperty(BlockStateProperties.FACING) && !blockEntity.inventory.getStackInSlot(0).isEmpty()) {
			poseStack.pushPose();
			ItemStack stack = blockEntity.inventory.getStackInSlot(0);
			int seed = stack.isEmpty() ? 187 : Item.getId(stack.getItem()) + stack.getDamageValue();
			BakedModel bakedmodel = this.itemRenderer.getModel(stack, blockEntity.getLevel(), null, seed);
			poseStack.translate(0.5D, 0.5D, 0.5D);
			poseStack.mulPose(blockState.getValue(BlockStateProperties.FACING).getRotation());
			poseStack.translate(0.001D, 0.125D, 0.001D);
			poseStack.mulPose(Axis.XP.rotationDegrees(90));
			poseStack.translate(0.0D, -0.125D, 0.0D);
			this.itemRenderer.render(stack, ItemDisplayContext.GROUND, false, poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY, bakedmodel);
			poseStack.popPose();
		}
	}
}
