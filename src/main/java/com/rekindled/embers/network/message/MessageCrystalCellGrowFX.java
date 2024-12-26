package com.rekindled.embers.network.message;

import java.util.Random;
import java.util.function.Supplier;

import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.particle.GlowParticleOptions;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

public class MessageCrystalCellGrowFX {

	public static Random random = new Random();
	public BlockPos pos = BlockPos.ZERO;
	double capacity = 0;

	public MessageCrystalCellGrowFX() {
		super();
	}

	public MessageCrystalCellGrowFX(BlockPos pos, double capacity) {
		super();
		this.pos = pos;
		this.capacity = capacity;
	}

	public static void encode(MessageCrystalCellGrowFX msg, FriendlyByteBuf buf) {
		buf.writeBlockPos(msg.pos);
		buf.writeDouble(msg.capacity);
	}

	public static MessageCrystalCellGrowFX decode(FriendlyByteBuf buf) {
		return new MessageCrystalCellGrowFX(buf.readBlockPos(), buf.readDouble());
	}

	public static void handle(MessageCrystalCellGrowFX msg, Supplier<NetworkEvent.Context> ctx) {
		if (ctx.get().getDirection().getReceptionSide().isClient()) {
			ctx.get().enqueueWork(() -> spawnParticles(msg));
		}
		ctx.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	@SuppressWarnings("resource")
	public static void spawnParticles(MessageCrystalCellGrowFX msg) {
		Level level = Minecraft.getInstance().level;
		double angle = random.nextDouble() * 2.0 * Math.PI;
		double x = msg.pos.getX() + 0.5 + 0.5 * Math.sin(angle);
		double z = msg.pos.getZ() + 0.5 + 0.5 * Math.cos(angle);
		double x2 = msg.pos.getX() + 0.5;
		double z2 = msg.pos.getZ() + 0.5;
		float layerHeight = 0.25f;
		float numLayers = 2 + (float) Math.floor(msg.capacity / 120000.0f);
		float height = layerHeight * numLayers;
		for (float i = 0; i < 72; i++) {
			float coeff = i / 72.0f;
			level.addParticle(GlowParticleOptions.EMBER_NOMOTION, x * (1.0f - coeff) + x2 * coeff, msg.pos.getY() + (1.0f - coeff) + (height / 2.0f + 1.5f) * coeff, z * (1.0f - coeff) + z2 * coeff, 0, 0, 0);
		}
		level.playLocalSound(x, msg.pos.getY() + 0.5, z, EmbersSounds.CRYSTAL_CELL_GROW.get(), SoundSource.BLOCKS, 1.0f, 1.0f + random.nextFloat(), false);
	}
}
