package com.rekindled.embers.particle;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.rekindled.embers.render.EmbersRenderTypes;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AlchemyCircleParticle extends TextureSheetParticle {

	public float rotScale = 0.02f;
	public static final Quaternionf QUAT = Axis.XP.rotationDegrees(90);

	public AlchemyCircleParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, AlchemyCircleParticleOptions pOptions) {
		super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
		this.hasPhysics = false;
		this.xd = 0;
		this.yd = 0;
		this.zd = 0;
		this.rCol = pOptions.getColor().x();
		this.gCol = pOptions.getColor().y();
		this.bCol = pOptions.getColor().z();
		this.roll = rotScale;
		this.quadSize = pOptions.getScale();
		this.lifetime = pOptions.getLifetime();
	}

	public float getQuadSize(float pScaleFactor) {
		if (this.age < 20)
			return this.quadSize * (float) Math.sin((this.age + pScaleFactor) * Math.PI / 40.0);
		if (this.age > this.lifetime - 20)
			return this.quadSize - this.quadSize * (float) Math.sin((this.age + 20 - this.lifetime + pScaleFactor) * Math.PI / 40.0);
		return this.quadSize;
	}

	public void tick() {
		super.tick();
		if (this.age < 20)
			this.alpha = (float) Math.sin((this.age) * Math.PI / 40.0);
		if (this.age > this.lifetime - 20)
			this.alpha = 1.0f - (float) Math.sin((this.age + 20 - this.lifetime) * Math.PI / 40.0);
		this.oRoll = this.roll;
		this.roll += rotScale;
	}

	public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
		Vec3 vec3 = pRenderInfo.getPosition();
		float f = (float)(Mth.lerp((double)pPartialTicks, this.xo, this.x) - vec3.x());
		float f1 = (float)(Mth.lerp((double)pPartialTicks, this.yo, this.y) - vec3.y());
		float f2 = (float)(Mth.lerp((double)pPartialTicks, this.zo, this.z) - vec3.z());
		Quaternionf quaternion;
		if (this.roll == 0.0F) {
			quaternion = QUAT;
		} else {
			quaternion = new Quaternionf(QUAT);
			quaternion.rotateZ(Mth.lerp(pPartialTicks, this.oRoll, this.roll));
		}

		Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
		vector3f1.rotate(quaternion);
		Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
		float f4 = this.getQuadSize(pPartialTicks);

		for (int i = 0; i < 4; ++i) {
			Vector3f vector3f = avector3f[i];
			vector3f.rotate(quaternion);
			vector3f.mul(f4);
			vector3f.add(f, f1, f2);
		}

		float f7 = this.getU0();
		float f8 = this.getU1();
		float f5 = this.getV0();
		float f6 = this.getV1();
		int j = this.getLightColor(pPartialTicks);
		pBuffer.vertex((double)avector3f[0].x(), (double)avector3f[0].y(), (double)avector3f[0].z()).uv(f8, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
		pBuffer.vertex((double)avector3f[1].x(), (double)avector3f[1].y(), (double)avector3f[1].z()).uv(f8, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
		pBuffer.vertex((double)avector3f[2].x(), (double)avector3f[2].y(), (double)avector3f[2].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
		pBuffer.vertex((double)avector3f[3].x(), (double)avector3f[3].y(), (double)avector3f[3].z()).uv(f7, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();

		pBuffer.vertex((double)avector3f[3].x(), (double)avector3f[3].y(), (double)avector3f[3].z()).uv(f7, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
		pBuffer.vertex((double)avector3f[2].x(), (double)avector3f[2].y(), (double)avector3f[2].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
		pBuffer.vertex((double)avector3f[1].x(), (double)avector3f[1].y(), (double)avector3f[1].z()).uv(f8, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
		pBuffer.vertex((double)avector3f[0].x(), (double)avector3f[0].y(), (double)avector3f[0].z()).uv(f8, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
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
	public static class Provider implements ParticleProvider.Sprite<AlchemyCircleParticleOptions> {
		public TextureSheetParticle createParticle(AlchemyCircleParticleOptions pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
			return new AlchemyCircleParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, pType);
		}
	}
}
