package com.rekindled.embers.fluidtypes;

import java.util.function.Consumer;

import org.joml.Vector3f;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;

public class ViscousFluidType extends EmbersFluidType {

	public ViscousFluidType(Properties properties, FluidInfo info) {
		super(properties, info);
	}

	@Override
	public boolean move(FluidState state, LivingEntity entity, Vec3 movementVector, double gravity) {
		//lava movement logic copied from LivingEntity
		double d8 = entity.getY();
		boolean flag = entity.getDeltaMovement().y <= 0.0D;
		entity.moveRelative(0.02F, movementVector);
		entity.move(MoverType.SELF, entity.getDeltaMovement());
		if (entity.getFluidTypeHeight(this) <= entity.getFluidJumpThreshold()) {
			entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.5D, (double)0.8F, 0.5D));
			Vec3 vec33 = entity.getFluidFallingAdjustedMovement(gravity, flag, entity.getDeltaMovement());
			entity.setDeltaMovement(vec33);
		} else {
			entity.setDeltaMovement(entity.getDeltaMovement().scale(0.5D));
		}

		if (!entity.isNoGravity()) {
			entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, -gravity / 4.0D, 0.0D));
		}

		Vec3 vec34 = entity.getDeltaMovement();
		if (entity.horizontalCollision && entity.isFree(vec34.x, vec34.y + (double)0.6F - entity.getY() + d8, vec34.z)) {
			entity.setDeltaMovement(vec34.x, (double)0.3F, vec34.z);
		}
		return true;
	}

	public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
		consumer.accept(new IClientFluidTypeExtensions() {
			@Override
			public ResourceLocation getStillTexture() {
				return TEXTURE_STILL;
			}

			@Override
			public ResourceLocation getFlowingTexture() {
				return TEXTURE_FLOW;
			}

			@Override
			public Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
				return FOG_COLOR;
			}

			@Override
			public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {
				RenderSystem.setShaderFogStart(fogStart);
				RenderSystem.setShaderFogEnd(fogEnd);
			}
		});
	}

	@Override
	public void setItemMovement(ItemEntity entity) {
		Vec3 vec3 = entity.getDeltaMovement();
		entity.setDeltaMovement(vec3.x * (double)0.95F, vec3.y + (double)(vec3.y < (double)0.06F ? 5.0E-4F : 0.0F), vec3.z * (double)0.95F);
	}
}