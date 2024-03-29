package com.rekindled.embers.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rekindled.embers.entity.EmberProjectileEntity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EmberProjectileRenderer extends EntityRenderer<EmberProjectileEntity> {

	public EmberProjectileRenderer(EntityRendererProvider.Context pContext) {
		super(pContext);
	}

	@Override
	public void render(EmberProjectileEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {}

	@Override
	public ResourceLocation getTextureLocation(EmberProjectileEntity pEntity) {
		return InventoryMenu.BLOCK_ATLAS;
	}
}
