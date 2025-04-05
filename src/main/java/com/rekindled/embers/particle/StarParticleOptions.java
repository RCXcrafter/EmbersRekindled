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

public class StarParticleOptions implements ParticleOptions {

	public static final float MIN_SCALE = 0.01F;
	public static final float MAX_SCALE = 4.0F;
	protected final ResourceLocation colorId;
	protected final Vector3f color;
	protected final float scale;
	public static final StarParticleOptions EMBER = new StarParticleOptions(EmbersColors.EMBER_ID, 2.0F);

	public static final Codec<StarParticleOptions> CODEC = RecordCodecBuilder.create((p_175793_) -> {
		return p_175793_.group(ResourceLocation.CODEC.fieldOf("color_id").forGetter((p_175797_) -> {
			return p_175797_.colorId;
		}), ExtraCodecs.VECTOR3F.fieldOf("color").forGetter((p_175797_) -> {
			return p_175797_.color;
		}), Codec.FLOAT.fieldOf("scale").forGetter((p_175795_) -> {
			return p_175795_.scale;
		})).apply(p_175793_, StarParticleOptions::new);
	});
	public static final ParticleOptions.Deserializer<StarParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<StarParticleOptions>() {
		public StarParticleOptions fromCommand(ParticleType<StarParticleOptions> p_123689_, StringReader p_123690_) throws CommandSyntaxException {
			Vector3f vector3f = StarParticleOptions.readVector3f(p_123690_);
			p_123690_.expect(' ');
			float f = p_123690_.readFloat();
			return new StarParticleOptions(vector3f, f);
		}

		public StarParticleOptions fromNetwork(ParticleType<StarParticleOptions> p_123692_, FriendlyByteBuf p_123693_) {
			return new StarParticleOptions(p_123693_.readResourceLocation(), StarParticleOptions.readVector3f(p_123693_), p_123693_.readFloat());
		}
	};

	public StarParticleOptions(ResourceLocation pColorId, Vector3f pColor, float pScale) {
		this.colorId = pColorId;
		this.color = pColor;
		this.scale = pScale;
	}

	public StarParticleOptions(Vector3f pColor, float pScale) {
		this(EmbersColors.CUSTOM_ID, pColor, pScale);
	}

	public StarParticleOptions(ResourceLocation pColorId, float pScale) {
		this(pColorId, EmbersColors.EMBER, pScale);
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
		return RegistryManager.STAR_PARTICLE.get();
	}
}
