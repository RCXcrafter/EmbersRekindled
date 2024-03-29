package com.rekindled.embers.blockentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.StamperBlockEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public class StamperBlockEntityRenderer implements BlockEntityRenderer<StamperBlockEntity> {

	public static BakedModel arm;
	private final ItemRenderer itemRenderer;

	public StamperBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {
		this.itemRenderer = pContext.getItemRenderer();
	}

	@Override
	public void render(StamperBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		BlockRenderDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRenderer();
		BlockState blockState = blockEntity.getLevel().getBlockState(blockEntity.getBlockPos());
		if (blockState.getBlock() == RegistryManager.STAMPER.get()) {
			poseStack.pushPose();
			float magnitude = 0;
			if (!blockEntity.powered){
				if (blockEntity.prevPowered) {
					magnitude = 1.0f - partialTick;
				}
			} else {
				if (!blockEntity.prevPowered) {
					magnitude = partialTick;
				} else {
					magnitude = 1;
				}
			}
			poseStack.translate(0, -0.001D - magnitude, 0);
			if (arm != null)
				blockrendererdispatcher.getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(Sheets.solidBlockSheet()), blockState, arm, 0.0f, 0.0f, 0.0f, packedLight, packedOverlay, ModelData.EMPTY, Sheets.solidBlockSheet());

			if (!blockEntity.stamp.getStackInSlot(0).isEmpty()) {
				ItemStack stack = blockEntity.stamp.getStackInSlot(0);
				int seed = stack.isEmpty() ? 187 : Item.getId(stack.getItem()) + stack.getDamageValue();
				BakedModel bakedmodel = this.itemRenderer.getModel(stack, blockEntity.getLevel(), null, seed);
				poseStack.translate(0.5D, -0.0234375D, 0.5);
				poseStack.mulPose(Axis.XP.rotationDegrees(90));
				poseStack.scale(0.75f, 0.75f, 0.75f);
				this.itemRenderer.render(stack, ItemDisplayContext.FIXED, false, poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY, bakedmodel);
			}
			poseStack.popPose();
		}
	}
}
