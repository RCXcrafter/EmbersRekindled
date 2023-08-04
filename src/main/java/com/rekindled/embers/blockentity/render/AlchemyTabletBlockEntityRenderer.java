package com.rekindled.embers.blockentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rekindled.embers.blockentity.AlchemyTabletBlockEntity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class AlchemyTabletBlockEntityRenderer implements BlockEntityRenderer<AlchemyTabletBlockEntity> {

	//public ResourceLocation texture = new ResourceLocation(Embers.MODID + ":textures/entity/alchemy_circle.png");
	private final ItemRenderer itemRenderer;

	public AlchemyTabletBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {
		this.itemRenderer = pContext.getItemRenderer();
	}

	@Override
	public void render(AlchemyTabletBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		if (blockEntity != null) {
			//render item
			if (!blockEntity.inventory.getStackInSlot(0).isEmpty()) {
				poseStack.pushPose();
				ItemStack stack = blockEntity.inventory.getStackInSlot(0);
				int seed = stack.isEmpty() ? 187 : Item.getId(stack.getItem()) + stack.getDamageValue();
				BakedModel bakedmodel = this.itemRenderer.getModel(stack, blockEntity.getLevel(), null, seed);
				float f2 = bakedmodel.getTransforms().getTransform(ItemDisplayContext.GROUND).scale.y();
				poseStack.translate(0.5D, (double)(0.25F * f2) + 0.8D, 0.5D);
				this.itemRenderer.render(stack, ItemDisplayContext.GROUND, false, poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY, bakedmodel);
				poseStack.popPose();
			}
			//render alchemy circle
			//due to transparency issues, the alchemy circle is now a particle
			/*
			if (blockEntity.process != 0) {
				float processSign = (blockEntity.progress > 0) ? 1 : -1;
				if (blockEntity.process == 20) {
					processSign = 0;
				}
				RenderSystem.setShaderTexture(0, texture);
				RenderSystem.disableCull();
				RenderSystem.enableTexture();
				VertexConsumer buffer = bufferSource.getBuffer(EmbersRenderTypes.BEAM);

				float x = 0;//blockEntity.getBlockPos().getX();
				float y = 0;//blockEntity.getBlockPos().getY();
				float z = 0;//blockEntity.getBlockPos().getZ();

				float scale = (float) Math.sin((blockEntity.process + (partialTick*processSign)) * Math.PI / 40.0);
				poseStack.translate(0.5, 1.0001, 0.5);


				poseStack.mulPose(Vector3f.YP.rotationDegrees(partialTick + EmbersClientEvents.ticks % 360));
				poseStack.scale(scale, scale, scale);

				float r = 1.0f;
				float g = 0.25f;
				float b = 0.0625f;
				float a = scale / 2.0f;
				int lightx = 0xF000F0;
				int lighty = 0xF000F0;

				Matrix4f matrix4f = poseStack.last().pose();
				for (float i = 0; i < 8; i ++) {
					buffer.vertex(matrix4f, -1, 0, -1).uv(0, 0).uv2(lightx, lighty).color(r, g, b, a).endVertex();
					buffer.vertex(matrix4f, -1, 0, 1).uv(0, 1).uv2(lightx, lighty).color(r, g, b, a).endVertex();
					buffer.vertex(matrix4f, 1, 0, 1).uv(1, 1).uv2(lightx, lighty).color(r, g, b, a).endVertex();
					buffer.vertex(matrix4f, 1, 0, -1).uv(1, 0).uv2(lightx, lighty).color(r, g, b, a).endVertex();




					//RenderUtil.renderAlchemyCircle(buffer, matrix4f, x+0.5f, y+1.0f+(i/1000f), z+0.5f, 1.0f, 0.25f, 0.0625f, visualProcess/40.0f, 0.4f*visualProcess/10.0f, EmbersClientEvents.ticks+partialTick);
				}

				RenderSystem.enableCull();
			}*/
		}
	}
}
