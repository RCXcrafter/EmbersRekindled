package com.rekindled.embers.network.message;

import java.util.Random;
import java.util.function.Supplier;

import com.mojang.math.Vector3f;
import com.rekindled.embers.particle.GlowParticleOptions;
import com.rekindled.embers.util.Misc;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

public class MessageEmberRayFX {
	public static Random random = new Random();
	double posX = 0, posY = 0, posZ = 0;
	double dX = 0, dY = 0, dZ = 0;
	double hitDistance = Double.POSITIVE_INFINITY;
	int packedColor;

	public MessageEmberRayFX() {
		super();
	}

	public MessageEmberRayFX(double x, double y, double z, double dX, double dY, double dZ, double hitDistance, int packedColor){
		super();
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		this.dX = dX;
		this.dY = dY;
		this.dZ = dZ;
		this.hitDistance = hitDistance;
		this.packedColor = packedColor;
	}

	public static void encode(MessageEmberRayFX msg, FriendlyByteBuf buf) {
		buf.writeDouble(msg.posX);
		buf.writeDouble(msg.posY);
		buf.writeDouble(msg.posZ);
		buf.writeDouble(msg.dX);
		buf.writeDouble(msg.dY);
		buf.writeDouble(msg.dZ);
		buf.writeDouble(msg.hitDistance);
		buf.writeInt(msg.packedColor);
	}

	public static MessageEmberRayFX decode(FriendlyByteBuf buf) {
		return new MessageEmberRayFX(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readInt());
	}

	@SuppressWarnings("resource")
	public static void handle(MessageEmberRayFX msg, Supplier<NetworkEvent.Context> ctx) {
		if (ctx.get().getDirection().getReceptionSide().isClient()) {
			ctx.get().enqueueWork(() -> {
				Level world = Minecraft.getInstance().level;
				double distance = Math.sqrt(msg.dX * msg.dX + msg.dY * msg.dY + msg.dZ * msg.dZ);
				double segments = distance * 4;
				Vector3f color = Misc.colorFromInt(msg.packedColor);
				GlowParticleOptions options = new GlowParticleOptions(color, 2.0F);
				for (double i = 0; i < segments; i++) {
					if (i >= msg.hitDistance * 4) {
						for (int k = 0; k < 80; k++) {
							world.addParticle(options, msg.posX, msg.posY, msg.posZ, 1.125f * (random.nextFloat() - 0.5f), 1.125f * (random.nextFloat() - 0.5f), 1.125f * (random.nextFloat() - 0.5f));
						}
						break;
					}
					for (int j = 0; j < 5; j++) {
						msg.posX += 0.2 * msg.dX / segments;
						msg.posY += 0.2 * msg.dY / segments;
						msg.posZ += 0.2 * msg.dZ / segments;
						world.addParticle(options, msg.posX, msg.posY, msg.posZ, 0, 0.000001, 0);
					}
				}
			});
		}
		ctx.get().setPacketHandled(true);
	}
}
