package com.rekindled.embers.network.message;

import java.util.function.Supplier;

import com.rekindled.embers.EmbersClientEvents;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class MessageWorldSeed {

	long seed;

	public MessageWorldSeed(long seed) {
		this.seed = seed;
	}

	public static void encode(MessageWorldSeed msg, FriendlyByteBuf buf) {
		buf.writeLong(msg.seed);
	}

	public static MessageWorldSeed decode(FriendlyByteBuf buf) {
		return new MessageWorldSeed(buf.readLong());
	}

	public static void handle(MessageWorldSeed msg, Supplier<NetworkEvent.Context> ctx) {
		if (ctx.get().getDirection().getReceptionSide().isClient()) {
			ctx.get().enqueueWork(() -> {
				EmbersClientEvents.seed = msg.seed;
			});
		}
		ctx.get().setPacketHandled(true);
	}
}
