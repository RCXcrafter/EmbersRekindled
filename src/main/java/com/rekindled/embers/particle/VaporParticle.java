package com.rekindled.embers.particle;

import com.rekindled.embers.render.EmbersRenderTypes;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VaporParticle extends TextureSheetParticle {

	public float minScale = 0.1f;
	public float maxScale = 2.0f;

	public VaporParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, VaporParticleOptions pOptions) {
		super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
		this.friction = 0.8F;
		float speed = random.nextFloat() * 0.5f + 0.7f;
		this.speedUpWhenYMotionIsBlocked = true;
		if (!pOptions.getMotion().equals(Vec3.ZERO))
			this.setParticleSpeed(pOptions.getMotion().x() * speed, pOptions.getMotion().y() * speed, pOptions.getMotion().z() * speed);
		this.rCol = pOptions.getColor().x();
		this.gCol = pOptions.getColor().y();
		this.bCol = pOptions.getColor().z();
		this.oRoll = 2.0f * (float) Math.PI;
		this.roll = this.oRoll + 0.5f;
		this.quadSize *= 0.5F * pOptions.getScale();
		double i = 6.0D / (this.random.nextDouble() * 0.5D + 0.5D);
		this.lifetime = (int)(i * pOptions.getScale());
	}

	public float getQuadSize(float pScaleFactor) {
		return (float)(minScale + (maxScale - minScale) * (-Math.cos((Math.max(this.age + pScaleFactor - 1, 0) / (this.lifetime - 1)) * 2.0f * Math.PI) + 1) / 2.0f) / 5.0f;
	}

	public void tick() {
		super.tick();
		this.alpha = 1.0f - (float)this.age / (float)this.lifetime;
		this.oRoll = this.roll;
		this.roll += 0.5f;
		this.yd += 0.04D;
	}

	@Override
	protected int getLightColor(float partialTicks) {
		return 0xF000F0;
	}

	@Override
	public ParticleRenderType getRenderType() {
		return EmbersRenderTypes.PARTICLE_SHEET_ADDITIVE;
	}

	@OnlyIn(Dist.CLIENT)
	public static class Provider implements ParticleProvider.Sprite<VaporParticleOptions> {
		public TextureSheetParticle createParticle(VaporParticleOptions pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
			return new VaporParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, pType);
		}
	}
}
