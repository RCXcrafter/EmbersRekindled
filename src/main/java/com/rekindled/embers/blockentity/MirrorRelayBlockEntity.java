package com.rekindled.embers.blockentity;

import java.util.Random;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.power.IEmberPacketProducer;
import com.rekindled.embers.api.power.IEmberPacketReceiver;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.entity.EmberPacketEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

public class MirrorRelayBlockEntity extends BlockEntity implements IEmberPacketProducer, IEmberPacketReceiver {

	public BlockPos target = null;
	public Random random = new Random();
	public boolean polled = false;
	public Vec3 incomingDirection = Vec3.ZERO;

	public MirrorRelayBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.MIRROR_RELAY_ENTITY.get(), pPos, pBlockState);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		if (nbt.contains("targetX")){
			target = new BlockPos(nbt.getInt("targetX"), nbt.getInt("targetY"), nbt.getInt("targetZ"));
		}
		incomingDirection = new Vec3(nbt.getDouble("incomingX"), nbt.getDouble("incomingY"), nbt.getDouble("incomingZ"));
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		if (target != null){
			nbt.putInt("targetX", target.getX());
			nbt.putInt("targetY", target.getY());
			nbt.putInt("targetZ", target.getZ());
		}
		nbt.putDouble("incomingX", incomingDirection.x);
		nbt.putDouble("incomingY", incomingDirection.y);
		nbt.putDouble("incomingZ", incomingDirection.z);
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag nbt = super.getUpdateTag();
		nbt.putDouble("incomingX", incomingDirection.x);
		nbt.putDouble("incomingY", incomingDirection.y);
		nbt.putDouble("incomingZ", incomingDirection.z);
		return nbt;
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (level instanceof ServerLevel)
			((ServerLevel) level).getChunkSource().blockChanged(worldPosition);
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
			Axis axis = level.getBlockState(worldPosition).getValue(BlockStateProperties.FACING).getAxis();
			packet.setLifetime(78);
			packet.dest = target;
			packet.pos = getBlockPos();
			setIncomingDirection(packet.getDeltaMovement());
			packet.setDeltaMovement(packet.getDeltaMovement().multiply(axis == Axis.X ? -1.7 : 1.7, axis == Axis.Y ? -1.7 : 1.7, axis == Axis.Z ? -1.7 : 1.7));
			level.playLocalSound(packet.getX(), packet.getY(), packet.getZ(), EmbersSounds.EMBER_RELAY.get(), SoundSource.BLOCKS, 1.0f, 1.0f, false);
		}
		return false;
	}

	@Override
	public void setIncomingDirection(Vec3 direction) {
		Axis axis = level.getBlockState(worldPosition).getValue(BlockStateProperties.FACING).getAxis();
		incomingDirection = direction.multiply(axis == Axis.X ? -1.7 : 1.7, axis == Axis.Y ? -1.7 : 1.7, axis == Axis.Z ? -1.7 : 1.7);
		this.setChanged();
	}

	@Override
	public void setTargetPosition(BlockPos pos, Direction side) {
		if (pos != worldPosition) {
			target = pos;
			this.setChanged();
		}
	}

	@Override
	public Vec3 getEmittingDirection(Direction side) {
		if (incomingDirection.equals(Vec3.ZERO))
			return EmberEmitterBlockEntity.getBurstVelocity(level.getBlockState(worldPosition).getValue(BlockStateProperties.FACING));
		return incomingDirection;
	}
}
