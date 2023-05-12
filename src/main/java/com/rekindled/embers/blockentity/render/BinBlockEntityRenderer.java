package com.rekindled.embers.blockentity.render;

import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.rekindled.embers.blockentity.BinBlockEntity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BinBlockEntityRenderer implements BlockEntityRenderer<BinBlockEntity> {

	private final ItemRenderer itemRenderer;
	Random random = new Random();

	public BinBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {
		this.itemRenderer = pContext.getItemRenderer();
	}

	@Override
	public void render(BinBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		if (blockEntity != null) {
			if (!blockEntity.inventory.getStackInSlot(0).isEmpty()) {
				ItemStack stack = blockEntity.inventory.getStackInSlot(0);
				int seed = stack.isEmpty() ? 187 : Item.getId(stack.getItem()) + stack.getDamageValue();
				random.setSeed(seed);
				BakedModel bakedmodel = this.itemRenderer.getModel(stack, blockEntity.getLevel(), null, seed);
				int itemCount = (int)Math.ceil((stack.getCount())/4.0);
				for (int i = 0; i < itemCount; i ++) {
					float f2 = bakedmodel.getTransforms().getTransform(ItemTransforms.TransformType.GROUND).scale.y();
					poseStack.pushPose();
					poseStack.translate(0.5, 0.1525+(i*0.05), 0.5);
					poseStack.mulPose(Vector3f.YP.rotationDegrees(random.nextFloat()*360.0f));
					poseStack.translate(-0.5, f2 < 0.4f ? 0.036 : 0.0, -0.5);
					poseStack.mulPose(Vector3f.XP.rotationDegrees(90));
					poseStack.translate(0.5+0.1*random.nextFloat(), -0.1875+0.1*random.nextFloat(), 0);
					poseStack.scale(1.5f, 1.5f, 1.5f);
					poseStack.translate(0, f2 < 0.4f ? 0.1 : 0.25, 0);
					this.itemRenderer.render(stack, ItemTransforms.TransformType.GROUND, false, poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY, bakedmodel);
					poseStack.popPose();
				}
			}
		}
	}
}
