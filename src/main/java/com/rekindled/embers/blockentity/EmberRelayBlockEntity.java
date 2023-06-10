package com.rekindled.embers.blockentity;

import java.util.Random;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.power.IEmberPacketProducer;
import com.rekindled.embers.api.power.IEmberPacketReceiver;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.entity.EmberPacketEntity;
import com.rekindled.embers.particle.GlowParticleOptions;
import com.rekindled.embers.particle.StarParticleOptions;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EmberRelayBlockEntity extends BlockEntity implements IEmberPacketProducer, IEmberPacketReceiver {

	public BlockPos target = null;
	public Random random = new Random();
	public boolean polled = false;

	public EmberRelayBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.EMBER_RELAY_ENTITY.get(), pPos, pBlockState);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		if (nbt.contains("targetX")){
			target = new BlockPos(nbt.getInt("targetX"), nbt.getInt("targetY"), nbt.getInt("targetZ"));
		}
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		if (target != null){
			nbt.putInt("targetX", target.getX());
			nbt.putInt("targetY", target.getY());
			nbt.putInt("targetZ", target.getZ());
		}
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag nbt = super.getUpdateTag();
		saveAdditional(nbt);
		return nbt;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public boolean hasRoomFor(double ember) {
		if (polled)
			return target != null;
		polled = true;
		if (target != null && level.isLoaded(target) && level.getBlockEntity(target) instanceof IEmberPacketReceiver targetBE) {
			boolean hasRoom = targetBE.hasRoomFor(ember);
			polled = false;
			return hasRoom;
		}
		polled = false;
		return false;
	}

	@Override
	public boolean onReceive(EmberPacketEntity packet) {
		if (target != null && packet.pos != getBlockPos()) {
			if (level instanceof ServerLevel serverLevel) {
				serverLevel.sendParticles(new StarParticleOptions(GlowParticleOptions.EMBER_COLOR, 3.5f + 0.5f * random.nextFloat()), getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5, 12, 0.0125f * (random.nextFloat() - 0.5f), 0.0125f * (random.nextFloat() - 0.5f), 0.0125f * (random.nextFloat() - 0.5f), 1.0);
			}
			packet.lifetime = 78;
			packet.dest = target;
			packet.pos = getBlockPos();
			packet.setDeltaMovement(packet.getDeltaMovement().scale(1.7));
			level.playLocalSound(packet.getX(), packet.getY(), packet.getZ(), EmbersSounds.EMBER_RELAY.get(), SoundSource.BLOCKS, 1.0f, 1.0f, false);
		}
		return false;
	}

	@Override
	public void setTargetPosition(BlockPos pos, Direction side) {
		if (pos != worldPosition) {
			target = pos;
			this.setChanged();
		}
	}

	@Override
	public Direction getEmittingDirection(Direction side) {
		return side;
	}
}
