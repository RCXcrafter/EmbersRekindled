package com.rekindled.embers.particle;

import java.util.Locale;

import org.joml.Vector3f;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.EmbersAPI;
import com.rekindled.embers.util.EmbersColors;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

public class SmokeParticleOptions implements ParticleOptions {

	public static final float MIN_SCALE = 0.01F;
	public static final float MAX_SCALE = 4.0F;
	protected final ResourceLocation colorId;
	protected final Vector3f color;
	protected final float scale;
	public static final SmokeParticleOptions SMOKE = new SmokeParticleOptions(EmbersColors.SMOKE_ID, 2.0F);
	public static final SmokeParticleOptions BIG_SMOKE = new SmokeParticleOptions(EmbersColors.SMOKE_ID, 5.0F); //a number 6 with extra dip

	public static final Codec<SmokeParticleOptions> CODEC = RecordCodecBuilder.create((p_175793_) -> {
		return p_175793_.group(ResourceLocation.CODEC.fieldOf("color_id").forGetter((p_175797_) -> {
			return p_175797_.colorId;
		}), ExtraCodecs.VECTOR3F.fieldOf("color").forGetter((p_175797_) -> {
			return p_175797_.color;
		}), Codec.FLOAT.fieldOf("scale").forGetter((p_175795_) -> {
			return p_175795_.scale;
		})).apply(p_175793_, SmokeParticleOptions::new);
	});
	public static final ParticleOptions.Deserializer<SmokeParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<SmokeParticleOptions>() {
		public SmokeParticleOptions fromCommand(ParticleType<SmokeParticleOptions> p_123689_, StringReader p_123690_) throws CommandSyntaxException {
			Vector3f vector3f = SmokeParticleOptions.readVector3f(p_123690_);
			p_123690_.expect(' ');
			float f = p_123690_.readFloat();
			return new SmokeParticleOptions(vector3f, f);
		}

		public SmokeParticleOptions fromNetwork(ParticleType<SmokeParticleOptions> p_123692_, FriendlyByteBuf p_123693_) {
			return new SmokeParticleOptions(p_123693_.readResourceLocation(), SmokeParticleOptions.readVector3f(p_123693_), p_123693_.readFloat());
		}
	};

	public SmokeParticleOptions(ResourceLocation pColorId, Vector3f pColor, float pScale) {
		this.colorId = pColorId;
		this.color = pColor;
		this.scale = pScale;
	}

	public SmokeParticleOptions(Vector3f pColor, float pScale) {
		this(EmbersColors.CUSTOM_ID, pColor, pScale);
	}

	public SmokeParticleOptions(ResourceLocation pColorId, float pScale) {
		this(pColorId, EmbersColors.SMOKE, pScale);
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
		pBuffer.writeResourceLocation(this.colorId);
		pBuffer.writeFloat(this.color.x());
		pBuffer.writeFloat(this.color.y());
		pBuffer.writeFloat(this.color.z());
		pBuffer.writeFloat(this.scale);
	}

	public String writeToString() {
		return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.color.x(), this.color.y(), this.color.z(), this.scale);
	}

	public Vector3f getColor() {
		return EmbersAPI.getColor(this.colorId, this.color);
	}

	public float getScale() {
		return this.scale;
	}

	@Override
	public ParticleType<?> getType() {
		return RegistryManager.SMOKE_PARTICLE.get();
	}
}
