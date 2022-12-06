package com.rekindled.embers.network.message;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class MessageTEUpdateRequest {
	public long pos = 0;

	public MessageTEUpdateRequest(long pos) {
		this.pos = pos;
	}

	public MessageTEUpdateRequest(BlockPos pos) {
		this.pos = pos.asLong();
	}

	public static void encode(MessageTEUpdateRequest msg, FriendlyByteBuf buf) {
		buf.writeLong(msg.pos);
	}

	public static MessageTEUpdateRequest decode(FriendlyByteBuf buf) {
		return new MessageTEUpdateRequest(buf.readLong());
	}

	public static void handle(MessageTEUpdateRequest msg, Supplier<NetworkEvent.Context> ctx) {
		if (ctx.get().getDirection().getReceptionSide().isServer()) {
			ctx.get().enqueueWork(() -> {
				ServerPlayer player = ctx.get().getSender();
				if (player != null && player.level != null) {
					BlockEntity blockEntity = player.level.getBlockEntity(BlockPos.of(msg.pos));
					if (blockEntity != null) {
						Packet<ClientGamePacketListener> packet = blockEntity.getUpdatePacket();
						if (packet != null) {
							player.connection.send(packet);
						}
					}
				}
			});
		}
		ctx.get().setPacketHandled(true);
	}
}
