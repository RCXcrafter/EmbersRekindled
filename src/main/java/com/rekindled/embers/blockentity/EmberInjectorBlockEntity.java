package com.rekindled.embers.blockentity;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.event.EmberEvent;
import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.api.tile.IEmberInjectable;
import com.rekindled.embers.api.upgrades.UpgradeContext;
import com.rekindled.embers.api.upgrades.UpgradeUtil;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.particle.GlowParticleOptions;
import com.rekindled.embers.power.DefaultEmberCapability;
import com.rekindled.embers.util.sound.ISoundController;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class EmberInjectorBlockEntity extends BlockEntity implements ISoundController {

	public IEmberCapability capability = new DefaultEmberCapability() {
		@Override
		public void onContentsChanged() {
			super.onContentsChanged();
			EmberInjectorBlockEntity.this.setChanged();
		}
	};
	protected int ticksExisted = 0;
	protected int progress = -1;
	static Random random = new Random();
	public static final double EMBER_COST = 1.0;
	public static final int MAX_DISTANCE = 2;

	public static final int SOUND_PROCESS = 1;
	public static final int[] SOUND_IDS = new int[]{SOUND_PROCESS};

	HashSet<Integer> soundsPlaying = new HashSet<>();
	public boolean isWorking;
	public int distance;

	public EmberInjectorBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.EMBER_INJECTOR_ENTITY.get(), pPos, pBlockState);
		capability.setEmberCapacity(24000);
		capability.setEmber(0);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		capability.deserializeNBT(nbt);
		isWorking = nbt.getBoolean("isWorking");
		distance = nbt.getInt("distance");
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		capability.writeToNBT(nbt);
		nbt.putBoolean("isWorking", isWorking);
		nbt.putInt("distance", distance);
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

	public static void clientTick(Level level, BlockPos pos, BlockState state, EmberInjectorBlockEntity blockEntity) {
		blockEntity.handleSound();
		if (blockEntity.isWorking) {
			Direction facing = state.getValue(BlockStateProperties.FACING);
			for (int i = 0; i < 6 * blockEntity.distance; i++) {
				level.addParticle(new GlowParticleOptions(GlowParticleOptions.EMBER_COLOR, 4.0F),
						pos.getX()+0.5f+random.nextFloat()*blockEntity.distance*facing.getNormal().getX(),
						pos.getY()+0.5f+random.nextFloat()*blockEntity.distance*facing.getNormal().getY(),
						pos.getZ()+0.5f+random.nextFloat()*blockEntity.distance*facing.getNormal().getZ(), 0, 0, 0);
			}
		}
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, EmberInjectorBlockEntity blockEntity) {
		List<UpgradeContext> upgrades = UpgradeUtil.getUpgrades(level, pos, Direction.values());
		UpgradeUtil.verifyUpgrades(blockEntity, upgrades);
		boolean wasWorking = blockEntity.isWorking;
		int previousDist = blockEntity.distance;
		if (!UpgradeUtil.doTick(blockEntity, upgrades)) {
			Direction facing = state.getValue(BlockStateProperties.FACING);
			int maxDist = UpgradeUtil.getOtherParameter(blockEntity, "distance", MAX_DISTANCE, upgrades);
			BlockPos hitPos = pos;
			BlockEntity tile = null;
			for (int i = 1; i <= maxDist; i++) {
				hitPos = hitPos.relative(facing);
				BlockState hitState = level.getBlockState(hitPos);
				if (!hitState.getCollisionShape(level, hitPos).isEmpty()) {
					tile = level.getBlockEntity(hitPos);
					blockEntity.distance = i;
					break;
				}
			}
			blockEntity.isWorking = false;
			double emberCost = UpgradeUtil.getTotalEmberConsumption(blockEntity, EMBER_COST, upgrades);
			if (tile instanceof IEmberInjectable injectable && injectable.isValid() && blockEntity.capability.getEmber() > emberCost) {
				boolean cancel = UpgradeUtil.doWork(blockEntity, upgrades);
				if (!cancel) {
					double enberInjected = EMBER_COST * UpgradeUtil.getTotalSpeedModifier(blockEntity, upgrades);
					injectable.inject(blockEntity, enberInjected);
					UpgradeUtil.throwEvent(blockEntity, new EmberEvent(blockEntity, EmberEvent.EnumType.CONSUME, emberCost), upgrades);
					blockEntity.isWorking = true;
					blockEntity.capability.removeAmount(emberCost, true);
				}
			}
		}
		if (wasWorking != blockEntity.isWorking || previousDist != blockEntity.distance) {
			blockEntity.setChanged();
			blockEntity.syncToClient();
		}
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (!this.remove && cap == EmbersCapabilities.EMBER_CAPABILITY) {
			return capability.getCapability(cap, side);
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		capability.invalidate();
	}

	public void syncToClient() {
		if (level instanceof ServerLevel serverLevel) {
			for (ServerPlayer serverplayer : serverLevel.getServer().getPlayerList().getPlayers()) {
				serverplayer.connection.send(this.getUpdatePacket());
			}
		}
	}

	@Override
	public void playSound(int id) {
		switch (id) {
		case SOUND_PROCESS:
			EmbersSounds.playMachineSound(this, SOUND_PROCESS, EmbersSounds.INJECTOR_LOOP.get(), SoundSource.BLOCKS, true, 1.0f, 1.0f, worldPosition.getX() + 0.5f, worldPosition.getY() + 0.5f, worldPosition.getZ() + 0.5f);
			break;
		}
		soundsPlaying.add(id);
	}

	@Override
	public void stopSound(int id) {
		soundsPlaying.remove(id);
	}

	@Override
	public boolean isSoundPlaying(int id) {
		return soundsPlaying.contains(id);
	}

	@Override
	public int[] getSoundIDs() {
		return SOUND_IDS;
	}

	@Override
	public boolean shouldPlaySound(int id) {
		return id == SOUND_PROCESS && isWorking;
	}
}
