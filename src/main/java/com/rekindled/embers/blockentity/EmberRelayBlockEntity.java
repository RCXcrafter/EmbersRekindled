package com.rekindled.embers.blockentity;

import java.util.HashSet;
import java.util.Random;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.power.IEmberPacketProducer;
import com.rekindled.embers.api.power.IEmberPacketReceiver;
import com.rekindled.embers.api.power.ITargetable;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.entity.EmberPacketEntity;
import com.rekindled.embers.particle.StarParticleOptions;
import com.rekindled.embers.util.EmbersColors;
import com.rekindled.embers.util.Misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

public class EmberRelayBlockEntity extends BlockEntity implements IEmberPacketProducer, ITargetable, IEmberPacketReceiver {

	public BlockPos target = null;
	public Random random = new Random();
	public boolean polled = false;
	public Vec3 incomingDirection = Vec3.ZERO;
	public HashSet<ChunkPos> trajectoryChunks = null;

	public EmberRelayBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.EMBER_RELAY_ENTITY.get(), pPos, pBlockState);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		if (nbt.contains("targetX")) {
			target = new BlockPos(nbt.getInt("targetX"), nbt.getInt("targetY"), nbt.getInt("targetZ"));
		}
		incomingDirection = new Vec3(nbt.getDouble("incomingX"), nbt.getDouble("incomingY"), nbt.getDouble("incomingZ"));
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		if (target != null) {
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
		if (target != null) {
			nbt.putInt("targetX", target.getX());
			nbt.putInt("targetY", target.getY());
			nbt.putInt("targetZ", target.getZ());
		}
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
		if (trajectoryChunks == null)
			trajectoryChunks = new HashSet<ChunkPos>();
		Misc.calculateTrajectoryChunks(trajectoryChunks, worldPosition, target, getEmittingDirection(level.getBlockState(worldPosition).getValue(BlockStateProperties.FACING).getOpposite()));
	}

	@Override
	public boolean hasRoomFor(double ember) {
		if (trajectoryChunks == null) {
			trajectoryChunks = new HashSet<ChunkPos>();
			Misc.calculateTrajectoryChunks(trajectoryChunks, worldPosition, target, getEmittingDirection(level.getBlockState(worldPosition).getValue(BlockStateProperties.FACING).getOpposite()));
		}
		if (level instanceof ServerLevel serverLevel) {
			for (ChunkPos chunk : trajectoryChunks) {
				if (!serverLevel.isNaturalSpawningAllowed(chunk))
					return false;
			}
		}
		if (polled)
			return target != null;
		polled = true;
		if (target != null && level.getBlockEntity(target) instanceof IEmberPacketReceiver targetBE) {
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
				serverLevel.sendParticles(new StarParticleOptions(EmbersColors.EMBER_ID, 3.5f + 0.5f * random.nextFloat()), getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5, 12, 0.0125f * (random.nextFloat() - 0.5f), 0.0125f * (random.nextFloat() - 0.5f), 0.0125f * (random.nextFloat() - 0.5f), 0.0);
			}
			packet.setLifetime(78);
			packet.dest = target;
			packet.pos = getBlockPos();
			setIncomingDirection(packet.getDeltaMovement());
			packet.setDeltaMovement(packet.getDeltaMovement().scale(1.7));
			level.playLocalSound(packet.getX(), packet.getY(), packet.getZ(), EmbersSounds.EMBER_RELAY.get(), SoundSource.BLOCKS, 1.0f, 1.0f, false);
		}
		return false;
	}

	@Override
	public void setIncomingDirection(Vec3 direction) {
		incomingDirection = direction.scale(1.7);
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
			return EmberEmitterBlockEntity.getBurstVelocity(side);
		return incomingDirection;
	}

	@Override
	public BlockPos getTarget(Direction side) {
		BlockState state = level.getBlockState(worldPosition);
		if (state.hasProperty(BlockStateProperties.FACING)) {
			Direction facing = state.getValue(BlockStateProperties.FACING);
			if (side != facing)
				return null;
		}
		return target;
	}
}
