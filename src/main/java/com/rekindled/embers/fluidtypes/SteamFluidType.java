package com.rekindled.embers.fluidtypes;

import java.util.function.Consumer;

import org.joml.Vector3f;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeMod;

public class SteamFluidType extends EmbersFluidType {

	public SteamFluidType(Properties properties, FluidInfo info) {
		super(properties, info);
	}

	@Override
	public boolean move(FluidState state, LivingEntity entity, Vec3 movementVector, double gravity) {
		entity.hurt(entity.level().damageSources().inFire(), 1.0F);

		entity.level().getProfiler().push("jump");
		if (entity.jumping) {
			boolean flag = entity.isInWater();
			if (!flag || entity.onGround()) {
				if (!entity.isInLava() || entity.onGround()) {
					if ((entity.onGround() || flag) && entity.noJumpDelay == 0) {
						jumpFromGround(entity);
						entity.noJumpDelay = 10;
					}
				} else {
					entity.jumpInFluid(ForgeMod.LAVA_TYPE.get());
				}
			} else {
				entity.jumpInFluid(ForgeMod.WATER_TYPE.get());
			}
		} else {
			entity.noJumpDelay = 0;
		}
		entity.level().getProfiler().pop();

		float f2 = 0.6f;
		float f3 = entity.onGround() ? f2 * 0.91F : 0.91F;
		Vec3 vec35 = entity.handleRelativeFrictionAndCalculateMovement(movementVector, f2);
		double d2 = vec35.y;
		if (entity.hasEffect(MobEffects.LEVITATION)) {
			d2 += (0.05D * (double)(entity.getEffect(MobEffects.LEVITATION).getAmplifier() + 1) - vec35.y) * 0.2D;
			entity.resetFallDistance();
		} else if (entity.level().isClientSide() && !entity.level().hasChunkAt(entity.blockPosition())) {
			if (entity.getY() > (double)entity.level().getMinBuildHeight()) {
				d2 = -0.1D;
			} else {
				d2 = 0.0D;
			}
		} else if (!entity.isNoGravity()) {
			d2 -= gravity;
		}

		if (entity.shouldDiscardFriction()) {
			vec35 = new Vec3(vec35.x, d2, vec35.z);
		} else {
			vec35 = new Vec3(vec35.x * (double)f3, d2 * (double)0.98F, vec35.z * (double)f3);
		}

		entity.setDeltaMovement(vec35);

		return true;
	}

	static void jumpFromGround(LivingEntity entity) {
		Vec3 vec3 = entity.getDeltaMovement();
		entity.setDeltaMovement(vec3.x, 0.42F + entity.getJumpBoostPower(), vec3.z);
		if (entity.isSprinting()) {
			float f = entity.getYRot() * ((float)Math.PI / 180F);
			entity.setDeltaMovement(entity.getDeltaMovement().add((double)(-Mth.sin(f) * 0.2F), 0.0D, (double)(Mth.cos(f) * 0.2F)));
		}

		entity.hasImpulse = true;
		ForgeHooks.onLivingJump(entity);
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
				float[] color = RenderSystem.getShaderFogColor();
				RenderSystem.setShaderFogColor(color[0], color[1], color[2], 0.7F);
				RenderSystem.setShaderFogEnd(fogEnd);
			}
		});
	}
}