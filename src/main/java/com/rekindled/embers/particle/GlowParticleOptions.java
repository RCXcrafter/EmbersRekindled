package com.rekindled.embers.particle;

import java.util.Locale;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Vector3f;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rekindled.embers.RegistryManager;

import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;

public class GlowParticleOptions implements ParticleOptions {

	public static final float MIN_SCALE = 0.01F;
	public static final float MAX_SCALE = 4.0F;
	protected final Vector3f color;
	protected final float scale;
	public static final Vector3f EMBER_COLOR = new Vector3f(255.0F / 255.0F, 64.0F / 255.0F, 16.0F / 255.0F);
	public static final GlowParticleOptions EMBER = new GlowParticleOptions(EMBER_COLOR, 2.0F);

	public static final Codec<GlowParticleOptions> CODEC = RecordCodecBuilder.create((p_175793_) -> {
		return p_175793_.group(Vector3f.CODEC.fieldOf("color").forGetter((p_175797_) -> {
			return p_175797_.color;
		}), Codec.FLOAT.fieldOf("scale").forGetter((p_175795_) -> {
			return p_175795_.scale;
		})).apply(p_175793_, GlowParticleOptions::new);
	});
	public static final ParticleOptions.Deserializer<GlowParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<GlowParticleOptions>() {
		public GlowParticleOptions fromCommand(ParticleType<GlowParticleOptions> p_123689_, StringReader p_123690_) throws CommandSyntaxException {
			Vector3f vector3f = GlowParticleOptions.readVector3f(p_123690_);
			p_123690_.expect(' ');
			float f = p_123690_.readFloat();
			return new GlowParticleOptions(vector3f, f);
		}

		public GlowParticleOptions fromNetwork(ParticleType<GlowParticleOptions> p_123692_, FriendlyByteBuf p_123693_) {
			return new GlowParticleOptions(GlowParticleOptions.readVector3f(p_123693_), p_123693_.readFloat());
		}
	};

	public GlowParticleOptions(Vector3f pColor, float pScale) {
		this.color = pColor;
		this.scale = Mth.clamp(pScale, 0.01F, 4.0F);
	}

	public static Vector3f readVector3f(StringReader pStringInput) throws CommandSyntaxException {
		pStringInput.expect(' ');
		float f = pStringInput.readFloat();
		pStringInput.expect(' ');
		float f1 = pStringInput.readFloat();
		pStringInput.expect(' ');
		float f2 = pStringInput.readFloat();
		return new Vector3f(f, f1, f2);
	}

	public static Vector3f readVector3f(FriendlyByteBuf pBuffer) {
		return new Vector3f(pBuffer.readFloat(), pBuffer.readFloat(), pBuffer.readFloat());
	}

	public void writeToNetwork(FriendlyByteBuf pBuffer) {
		pBuffer.writeFloat(this.color.x());
		pBuffer.writeFloat(this.color.y());
		pBuffer.writeFloat(this.color.z());
		pBuffer.writeFloat(this.scale);
	}

	public String writeToString() {
		return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", Registry.PARTICLE_TYPE.getKey(this.getType()), this.color.x(), this.color.y(), this.color.z(), this.scale);
	}

	public Vector3f getColor() {
		return this.color;
	}

	public float getScale() {
		return this.scale;
	}

	@Override
	public ParticleType<?> getType() {
		return RegistryManager.GLOW_PARTICLE.get();
	}
}
