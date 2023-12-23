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
public class XRayGlowParticle extends TextureSheetParticle {

	public XRayGlowParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, XRayGlowParticleOptions pOptions) {
		super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
		this.friction = 0.96F;
		this.speedUpWhenYMotionIsBlocked = true;
		this.hasPhysics = false;
		if (!pOptions.getMotion().equals(Vec3.ZERO)) {
			this.setParticleSpeed(pOptions.getMotion().x(), pOptions.getMotion().y(), pOptions.getMotion().z());
		} else if (pXSpeed != 0 || pYSpeed != 0 || pZSpeed != 0) {
			this.xd = pXSpeed;
			this.yd = pYSpeed;
			this.zd = pZSpeed;
		} else {
			this.xd = (Math.random() * 2.0D - 1.0D) * (double)0.4F;
			this.yd = (Math.random() * 2.0D - 1.0D) * (double)0.4F;
			this.zd = (Math.random() * 2.0D - 1.0D) * (double)0.4F;
		}
		this.xd *= (double)0.1F;
		this.yd *= (double)0.1F;
		this.zd *= (double)0.1F;
		this.rCol = pOptions.getColor().x();
		this.gCol = pOptions.getColor().y();
		this.bCol = pOptions.getColor().z();
		this.oRoll = random.nextFloat();
		this.roll = this.oRoll + 1.0f;
		this.quadSize *= 0.75F * pOptions.getScale();
		double i = 4.5D / (this.random.nextDouble() * 0.3D + 0.7D);
		if (pOptions.getLifetime() > 0) {
			this.lifetime = pOptions.getLifetime();
		} else {
			this.lifetime = (int)Math.max((float)i * pOptions.getScale(), 1.0F);
		}
	}

	public float getQuadSize(float pScaleFactor) {
		return this.quadSize - this.quadSize * (((float)this.age + pScaleFactor) / (float)this.lifetime);
	}

	public void tick() {
		super.tick();
		this.alpha = 1.0f - (float)this.age / (float)this.lifetime;
		this.oRoll = this.roll;
		this.roll += 1.0f;
	}

	@Override
	protected int getLightColor(float partialTicks) {
		return 0xF000F0;
	}

	@Override
	public ParticleRenderType getRenderType() {
		return EmbersRenderTypes.PARTICLE_SHEET_ADDITIVE_XRAY;
	}

	@OnlyIn(Dist.CLIENT)
	public static class Provider implements ParticleProvider.Sprite<XRayGlowParticleOptions> {
		public TextureSheetParticle createParticle(XRayGlowParticleOptions pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
			return new XRayGlowParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, pType);
		}
	}
}
