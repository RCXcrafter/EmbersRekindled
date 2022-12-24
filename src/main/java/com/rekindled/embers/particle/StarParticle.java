package com.rekindled.embers.particle;

import com.rekindled.embers.render.EmbersRenderTypes;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StarParticle extends TextureSheetParticle {
	
	public float rotScale = random.nextFloat() * 0.1f + 0.05f;

	public StarParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, StarParticleOptions pOptions, SpriteSet pSprites) {
		super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
		this.friction = 0.6F;
		this.speedUpWhenYMotionIsBlocked = true;
		this.hasPhysics = false;
		this.xd *= (double)0.1F;
		this.yd *= (double)0.1F;
		this.zd *= (double)0.1F;
		this.rCol = pOptions.getColor().x();
		this.gCol = pOptions.getColor().y();
		this.bCol = pOptions.getColor().z();
		this.roll = rotScale;
		this.quadSize *= 0.75F * pOptions.getScale();
		double i = 6.0D / (this.random.nextDouble() * 0.5D + 0.5D);
		this.lifetime = (int)(i * pOptions.getScale());
		this.pickSprite(pSprites);
	}

	public float getQuadSize(float pScaleFactor) {
		return this.quadSize - this.quadSize * (((float)this.age + pScaleFactor) / (float)this.lifetime);
	}

	public void tick() {
		super.tick();
		this.alpha = 1.0f - (float)this.age / (float)this.lifetime;
		this.oRoll = this.roll;
		this.roll += rotScale;
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
	public static class Provider implements ParticleProvider<StarParticleOptions> {
		private final SpriteSet sprites;

		public Provider(SpriteSet pSprites) {
			this.sprites = pSprites;
		}

		public Particle createParticle(StarParticleOptions pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
			return new StarParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, pType, this.sprites);
		}
	}
}
