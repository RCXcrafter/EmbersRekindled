package com.rekindled.embers.blockentity;

import java.util.Random;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.api.power.IEmberPacketReceiver;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.entity.EmberPacketEntity;
import com.rekindled.embers.particle.GlowParticleOptions;
import com.rekindled.embers.particle.SmokeParticleOptions;
import com.rekindled.embers.particle.SparkParticleOptions;
import com.rekindled.embers.particle.StarParticleOptions;
import com.rekindled.embers.power.DefaultEmberCapability;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.common.util.LazyOptional;

// FIXME: Transfer options into mod options, i placed it here temporary
class Options {

	public static double mulitiplier = 100D;
	public static boolean forgeEnergyCanGenerateEmbers = false;
	public static boolean embersEnergyCanGenerateForgeEnergy = true;
	public static boolean ae2EnergyCanGenerateEmbers = false;
	public static boolean embersEnergyCanGenerateAE2Energy = true;
	public static boolean rfEnergyCanGenerateEmbers = false;
	public static boolean embersEnergyCanGenerateRFEnergy = true;

}

public class EmberReceiverBlockEntity extends BlockEntity implements IEmberPacketReceiver {

	public IEmberCapability capability = new DefaultEmberCapability() {
		@Override
		public void onContentsChanged() {
			super.onContentsChanged();
			EmberReceiverBlockEntity.this.setChanged();
		}

		@Override
		public boolean acceptsVolatile() {
			return false;
		}
	};

	public static final int TRANSFER_RATE = 10;

	public long ticksExisted = 0;
	public Random random = new Random();

	public EmberReceiverBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.EMBER_RECEIVER_ENTITY.get(), pPos, pBlockState);
		capability.setEmberCapacity(2000);
	}

	public EmberReceiverBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
		super(pType, pPos, pBlockState);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		capability.deserializeNBT(nbt);
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
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

	public static void serverTick(Level level, BlockPos pos, BlockState state, EmberReceiverBlockEntity blockEntity) {
		blockEntity.ticksExisted ++;
		Direction facing = state.getValue(BlockStateProperties.FACING);
		BlockEntity attachedTile = level.getBlockEntity(pos.relative(facing, -1));
		if (blockEntity.ticksExisted % 2 == 0 && attachedTile != null){
			IEmberCapability cap = attachedTile.getCapability(EmbersCapabilities.EMBER_CAPABILITY, facing).orElse(null);
			IEnergyStorage fe_cap = attachedTile.getCapability(ForgeCapabilities.ENERGY, facing).orElse(null);
			if (cap != null) {
				if (cap.getEmber() < cap.getEmberCapacity() && blockEntity.capability.getEmber() > 0){
					double added = cap.addAmount(Math.min(TRANSFER_RATE, blockEntity.capability.getEmber()), true);
					blockEntity.capability.removeAmount(added, true);
				}
			}
			else if (Options.embersEnergyCanGenerateForgeEnergy && fe_cap != null) {
				if (fe_cap.canReceive() && fe_cap.getEnergyStored() < fe_cap.getMaxEnergyStored()) {
					int added = fe_cap.receiveEnergy((int) Math.min(TRANSFER_RATE * Options.mulitiplier, blockEntity.capability.getEmber() * Options.mulitiplier), true);
					if (added > 0) {
						fe_cap.receiveEnergy((int) Math.min(TRANSFER_RATE * Options.mulitiplier, blockEntity.capability.getEmber() * Options.mulitiplier), false);
						blockEntity.capability.removeAmount(added / Options.mulitiplier, true);
					}
				}
			}
		}
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (!this.remove && cap == EmbersCapabilities.EMBER_CAPABILITY) {
			return capability.getCapability(cap, side);
		}
		return super.getCapability(cap, side);
	}

	/*@Override
	public boolean isFull() {
		return capability.getEmber() >= capability.getEmberCapacity();
	}*/

	@Override
	public boolean hasRoomFor(double ember) {
		return capability.getEmber() * 2 <= capability.getEmberCapacity();
	}

	@Override
	public boolean onReceive(EmberPacketEntity packet) {
		if (level instanceof ServerLevel serverLevel) {
			if (capability.getEmber() + packet.value > capability.getEmberCapacity()) {
				serverLevel.sendParticles(new SparkParticleOptions(GlowParticleOptions.EMBER_COLOR, random.nextFloat() * 0.75f + 0.45f), getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5, 5, 0.125f * (random.nextFloat() - 0.5f), 0.125f * (random.nextFloat()), 0.125f * (random.nextFloat() - 0.5f), 1.0);
				serverLevel.sendParticles(new SmokeParticleOptions(SmokeParticleOptions.SMOKE_COLOR, 2.0f + random.nextFloat() * 2.0f), getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5, 15, 0.0625f * (random.nextFloat() - 0.5f), 0.0625f + 0.0625f * (random.nextFloat() - 0.5f), 0.0625f * (random.nextFloat() - 0.5f), 1.0);
			} else {
				serverLevel.sendParticles(new StarParticleOptions(GlowParticleOptions.EMBER_COLOR, 3.5f + 0.5f * random.nextFloat()), getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5, 12, 0.0125f * (random.nextFloat() - 0.5f), 0.0125f * (random.nextFloat() - 0.5f), 0.0125f * (random.nextFloat() - 0.5f), 0.0);
			}
		}
		level.playLocalSound(packet.getX(), packet.getY(), packet.getZ(), packet.value >= 100 ? EmbersSounds.EMBER_RECEIVE_BIG.get() : EmbersSounds.EMBER_RECEIVE.get(), SoundSource.BLOCKS, 1.0f, 1.0f, false);
		return true;
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		capability.invalidate();
	}
}
