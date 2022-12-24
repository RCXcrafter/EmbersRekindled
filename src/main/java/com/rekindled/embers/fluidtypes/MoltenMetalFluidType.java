package com.rekindled.embers.fluidtypes;

import java.awt.Color;
import java.util.function.Consumer;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Vector3f;
import com.rekindled.embers.Embers;
import com.rekindled.embers.fluidtypes.EmbersFluidType.FluidInfo;

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
import net.minecraftforge.fluids.FluidType;

public class MoltenMetalFluidType extends FluidType {

	public final ResourceLocation TEXTURE_STILL;
	public final ResourceLocation TEXTURE_FLOW;
	public final Vector3f FOG_COLOR;
	public final float fogStart;
	public final float fogEnd;

	public MoltenMetalFluidType(Properties properties, FluidInfo info) {
		super(properties);
		TEXTURE_STILL = new ResourceLocation(Embers.MODID, "block/fluid/" + info.name + "_still");
		TEXTURE_FLOW = new ResourceLocation(Embers.MODID, "block/fluid/" + info.name + "_flow");
		Color colorObject = new Color(info.color);
		FOG_COLOR = new Vector3f(colorObject.getRed()/255F, colorObject.getGreen()/255F, colorObject.getBlue()/255F);
		fogStart = info.fogStart;
		fogEnd = info.fogEnd;
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

		entity.lavaHurt();
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
		entity.lavaHurt();
		Vec3 vec3 = entity.getDeltaMovement();
		entity.setDeltaMovement(vec3.x * (double)0.95F, vec3.y + (double)(vec3.y < (double)0.06F ? 5.0E-4F : 0.0F), vec3.z * (double)0.95F);
	}
}