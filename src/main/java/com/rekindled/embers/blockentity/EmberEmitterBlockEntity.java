package com.rekindled.embers.blockentity;

import java.util.Random;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.api.power.IEmberPacketProducer;
import com.rekindled.embers.api.power.IEmberPacketReceiver;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.entity.EmberPacketEntity;
import com.rekindled.embers.power.DefaultEmberCapability;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class EmberEmitterBlockEntity extends BlockEntity implements IEmberPacketProducer {

	public IEmberCapability capability = new DefaultEmberCapability() {
		@Override
		public void onContentsChanged() {
			super.onContentsChanged();
			EmberEmitterBlockEntity.this.setChanged();
		}

		@Override
		public boolean acceptsVolatile() {
			return false;
		}
	};

	public static final double TRANSFER_RATE = 40.0;
	public static final double PULL_RATE = 10.0;

	public BlockPos target = null;
	public long ticksExisted = 0;
	public Random random = new Random();
	public int offset = random.nextInt(40);

	public EmberEmitterBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.EMBER_EMITTER_ENTITY.get(), pPos, pBlockState);
		capability.setEmberCapacity(200);
		capability.setEmber(0);
	}

	public EmberEmitterBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
		super(pType, pPos, pBlockState);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		if (nbt.contains("targetX")){
			target = new BlockPos(nbt.getInt("targetX"), nbt.getInt("targetY"), nbt.getInt("targetZ"));
		}
		capability.deserializeNBT(nbt);
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		if (target != null){
			nbt.putInt("targetX", target.getX());
			nbt.putInt("targetY", target.getY());
			nbt.putInt("targetZ", target.getZ());
		}
		capability.writeToNBT(nbt);
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

	public static void serverTick(Level level, BlockPos pos, BlockState state, EmberEmitterBlockEntity blockEntity) {
		blockEntity.ticksExisted ++;
		Direction facing = state.getValue(BlockStateProperties.FACING);
		BlockEntity attachedTile = level.getBlockEntity(pos.relative(facing, -1));
		if (blockEntity.ticksExisted % 5 == 0 && attachedTile != null) {
			IEmberCapability cap = attachedTile.getCapability(EmbersCapabilities.EMBER_CAPABILITY, facing).orElse(null);
			if (cap != null) {
				if (cap.getEmber() > 0 && blockEntity.capability.getEmber() < blockEntity.capability.getEmberCapacity()){
					double removed = cap.removeAmount(PULL_RATE, true);
					blockEntity.capability.addAmount(removed, true);
				}
			}
		}
		if ((blockEntity.ticksExisted + blockEntity.offset) % 20 == 0 && level.hasNeighborSignal(pos) && blockEntity.target != null && level.isLoaded(blockEntity.target) && !level.isClientSide && blockEntity.capability.getEmber() > PULL_RATE) {
			BlockEntity targetTile = level.getBlockEntity(blockEntity.target);
			if (targetTile instanceof IEmberPacketReceiver){
				if (((IEmberPacketReceiver) targetTile).hasRoomFor(TRANSFER_RATE)){
					EmberPacketEntity packet = RegistryManager.EMBER_PACKET.get().create(blockEntity.level);
					Vec3 velocity = getBurstVelocity(facing);
					packet.initCustom(pos, blockEntity.target, velocity.x, velocity.y, velocity.z, Math.min(TRANSFER_RATE, blockEntity.capability.getEmber()));
					blockEntity.capability.removeAmount(Math.min(TRANSFER_RATE, blockEntity.capability.getEmber()), true);
					blockEntity.level.addFreshEntity(packet);
					level.playSound(null, pos, EmbersSounds.EMBER_EMIT.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
				}
			}
		}
	}

	public static Vec3 getBurstVelocity(Direction facing) {
		switch(facing) {
		case DOWN:
			return new Vec3(0, -0.5, 0);
		case UP:
			return new Vec3(0, 0.5, 0);
		case NORTH:
			return new Vec3(0, -0.01, -0.5);
		case SOUTH:
			return new Vec3(0, -0.01, 0.5);
		case WEST:
			return new Vec3(-0.5, -0.01, 0);
		case EAST:
			return new Vec3(0.5, -0.01, 0);
		default:
			return Vec3.ZERO;
		}
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (!this.remove && cap == EmbersCapabilities.EMBER_CAPABILITY && level.getBlockState(this.getBlockPos()).getValue(BlockStateProperties.FACING) != side) {
			return capability.getCapability(cap, side);
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		capability.invalidate();
	}

	@Override
	public void setTargetPosition(BlockPos pos, Direction side) {
		target = pos;
		this.setChanged();
	}

	@Override
	public Direction getEmittingDirection(Direction side) {
		return level.getBlockState(worldPosition).getValue(BlockStateProperties.FACING);
	}
}
