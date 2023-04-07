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
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

public class BeamSplitterBlockEntity extends BlockEntity implements IEmberPacketProducer, IEmberPacketReceiver {

	public BlockPos target1 = null;
	public BlockPos target2 = null;
	public Random random = new Random();
	public boolean polled = false;

	public BeamSplitterBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.BEAM_SPLITTER_ENTITY.get(), pPos, pBlockState);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		if (nbt.contains("target1X")){
			target1 = new BlockPos(nbt.getInt("target1X"), nbt.getInt("target1Y"), nbt.getInt("target1Z"));
		}
		if (nbt.contains("target2X")){
			target2 = new BlockPos(nbt.getInt("target2X"), nbt.getInt("target2Y"), nbt.getInt("target2Z"));
		}
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		if (target1 != null){
			nbt.putInt("target1X", target1.getX());
			nbt.putInt("target1Y", target1.getY());
			nbt.putInt("target1Z", target1.getZ());
		}
		if (target2 != null){
			nbt.putInt("target2X", target2.getX());
			nbt.putInt("target2Y", target2.getY());
			nbt.putInt("target2Z", target2.getZ());
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
			return target1 != null && target2 != null;
		polled = true;
		if (target1 != null && target2 != null && level.isLoaded(target1) && level.isLoaded(target2) && level.getBlockEntity(target1) instanceof IEmberPacketReceiver targetBE1 && level.getBlockEntity(target2) instanceof IEmberPacketReceiver targetBE2) {
			boolean hasRoom = targetBE1.hasRoomFor(ember / 2.0) && targetBE2.hasRoomFor(ember / 2.0);
			polled = false;
			return hasRoom;
		}
		polled = false;
		return false;
	}

	@Override
	public boolean onReceive(EmberPacketEntity packet) {
		if (target1 != null && target2 != null && packet.pos != getBlockPos()) {
			Axis axis = level.getBlockState(worldPosition).getValue(BlockStateProperties.AXIS);

			EmberPacketEntity packet1 = RegistryManager.EMBER_PACKET.get().create(level);
			Vec3 velocity1 = EmberEmitterBlockEntity.getBurstVelocity(Direction.get(AxisDirection.POSITIVE, axis));
			packet1.initCustom(worldPosition, target1, velocity1.x, velocity1.y, velocity1.z, packet.value / 2.0);
			level.addFreshEntity(packet1);

			EmberPacketEntity packet2 = RegistryManager.EMBER_PACKET.get().create(level);
			Vec3 velocity2 = EmberEmitterBlockEntity.getBurstVelocity(Direction.get(AxisDirection.NEGATIVE, axis));
			packet2.initCustom(worldPosition, target2, velocity2.x, velocity2.y, velocity2.z, packet.value / 2.0);
			level.addFreshEntity(packet2);

			level.playSound(null, worldPosition, packet.value >= 100 ? EmbersSounds.EMBER_EMIT_BIG.get() : EmbersSounds.EMBER_EMIT.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
		}
		return true;
	}

	@Override
	public void setTargetPosition(BlockPos pos, Direction side) {
		if (pos != worldPosition && side.getAxis() == level.getBlockState(worldPosition).getValue(BlockStateProperties.AXIS)) {
			if (side.getAxisDirection() == AxisDirection.POSITIVE) {
				target1 = pos;
			} else {
				target2 = pos;
			}
			this.setChanged();
		}
	}
}
