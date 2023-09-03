package com.rekindled.embers.particle;

import com.rekindled.embers.EmbersClientEvents;
import com.rekindled.embers.render.EmbersRenderTypes;
import com.rekindled.embers.util.Misc;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TyrfingParticle extends TextureSheetParticle {

	public int phase = Misc.random.nextInt(360);

	public TyrfingParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, TyrfingParticleOptions pOptions) {
		super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
		this.friction = 1.0F;
		this.speedUpWhenYMotionIsBlocked = true;
		this.hasPhysics = true;
		if (!pOptions.getMotion().equals(Vec3.ZERO)) {
			this.setParticleSpeed(pOptions.getMotion().x(), pOptions.getMotion().y(), pOptions.getMotion().z());
		} else if (pXSpeed != 0 || pYSpeed != 0 || pZSpeed != 0) {
			this.xd = pXSpeed;
			this.yd = pYSpeed;
			this.zd = pZSpeed;
		} else {
			this.xd = (Misc.random.nextFloat() - 0.5) * 0.75;
			this.yd = (Misc.random.nextFloat() - 0.5) * 0.75;
			this.zd = (Misc.random.nextFloat() - 0.5) * 0.75;
		}
		this.xd *= (double)0.1F;
		this.yd *= (double)0.1F;
		this.zd *= (double)0.1F;
		this.oRoll = random.nextFloat();
		this.roll = this.oRoll + 1.0f;
		this.quadSize *= 0.75F * pOptions.getScale();
		float timerSine = ((float)Math.sin(8.0*Math.toRadians(EmbersClientEvents.ticks % 360 + phase))+1.0f)/2.0f;
		this.rCol = (0.25f*timerSine);
		this.gCol = 0.0625f;
		this.bCol = (0.125f+0.125f*timerSine);
		if (pOptions.getLifetime() > 0) {
			this.lifetime = pOptions.getLifetime();
		} else {
			this.lifetime = (int)Math.max(Misc.random.nextInt(8) * 2 * pOptions.getScale(), 1.0F);
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
		float timerSine = ((float)Math.sin(8.0*Math.toRadians(EmbersClientEvents.ticks % 360 + phase))+1.0f)/2.0f;
		this.rCol = (0.25f*timerSine);
		this.gCol = 0.0625f;
		this.bCol = (0.125f+0.125f*timerSine);
	}

	@Override
	protected int getLightColor(float partialTicks) {
		return 0xF000F0;
	}

	@Override
	public ParticleRenderType getRenderType() {
		return EmbersRenderTypes.PARTICLE_SHEET_TRANSLUCENT_NODEPTH;
	}

	@OnlyIn(Dist.CLIENT)
	public static class Provider implements ParticleProvider.Sprite<TyrfingParticleOptions> {
		public TextureSheetParticle createParticle(TyrfingParticleOptions pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
			return new TyrfingParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, pType);
		}
	}
}
