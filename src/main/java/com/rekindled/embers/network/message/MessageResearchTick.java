package com.rekindled.embers.network.message;

import java.util.function.Supplier;

import com.rekindled.embers.research.ResearchManager;
import com.rekindled.embers.research.capability.IResearchCapability;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class MessageResearchTick {
	public ResourceLocation research;
	public boolean ticked;

	public MessageResearchTick(ResourceLocation research, boolean ticked) {
		this.research = research;
		this.ticked = ticked;
	}

	public static void encode(MessageResearchTick msg, FriendlyByteBuf buf) {
		buf.writeResourceLocation(msg.research);
		buf.writeBoolean(msg.ticked);
	}

	public static MessageResearchTick decode(FriendlyByteBuf buf) {
		return new MessageResearchTick(buf.readResourceLocation(), buf.readBoolean());
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
