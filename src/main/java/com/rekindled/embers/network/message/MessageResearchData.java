package com.rekindled.embers.network.message;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.rekindled.embers.research.ResearchManager;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class MessageResearchData {
	public static final int NAME_MAX_LENGTH = 64;
	Map<String,Boolean> ticks;

	public MessageResearchData() {
		this.ticks = new HashMap<>();
	}

	public MessageResearchData(Map<String, Boolean> ticks) {
		this.ticks = ticks;
	}

	public static void encode(MessageResearchData msg, FriendlyByteBuf buf) {
		buf.writeInt(msg.ticks.size());
		for (Map.Entry<String,Boolean> entry : msg.ticks.entrySet()) {
			buf.writeUtf(entry.getKey(), NAME_MAX_LENGTH);
			buf.writeBoolean(entry.getValue());
		}
	}

	public static MessageResearchData decode(FriendlyByteBuf buf) {
		Map<String,Boolean> ticks = new HashMap<>();
		int entries = buf.readInt();
		for(int i = 0; i < entries; i++) {
			String key = buf.readUtf(NAME_MAX_LENGTH);
			boolean value = buf.readBoolean();
			ticks.put(key, value);
		}
		return new MessageResearchData(ticks);
	}

	public static void handle(MessageResearchData msg, Supplier<NetworkEvent.Context> ctx) {
		if (ctx.get().getDirection().getReceptionSide().isClient()) {
			ctx.get().enqueueWork(() -> {
				ResearchManager.receiveResearchData(msg.ticks);
			});
		}
		ctx.get().setPacketHandled(true);
	}
}
