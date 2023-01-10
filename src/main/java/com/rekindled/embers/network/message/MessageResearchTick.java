package com.rekindled.embers.network.message;

import java.util.function.Supplier;

import com.rekindled.embers.research.ResearchManager;
import com.rekindled.embers.research.capability.IResearchCapability;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class MessageResearchTick {
	public static final int NAME_MAX_LENGTH = 64;
	public String research;
	public boolean ticked;

	public MessageResearchTick(String research, boolean ticked) {
		this.research = research;
		this.ticked = ticked;
	}

	public static void encode(MessageResearchTick msg, FriendlyByteBuf buf) {
		buf.writeUtf(msg.research, NAME_MAX_LENGTH);
		buf.writeBoolean(msg.ticked);
	}

	public static MessageResearchTick decode(FriendlyByteBuf buf) {
		return new MessageResearchTick(buf.readUtf(NAME_MAX_LENGTH), buf.readBoolean());
	}

	public static void handle(MessageResearchTick msg, Supplier<NetworkEvent.Context> ctx) {
		if (ctx.get().getDirection().getReceptionSide().isServer()) {
			ctx.get().enqueueWork(() -> {
				ServerPlayer player = ctx.get().getSender();
				IResearchCapability research = ResearchManager.getPlayerResearch(player);
                if(research != null) {
                    research.setCheckmark(msg.research, msg.ticked);
                    ResearchManager.sendResearchData(player);
                }
			});
		}
		ctx.get().setPacketHandled(true);
	}
}
