package com.rekindled.embers.blockentity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.event.DialInformationEvent;
import com.rekindled.embers.api.event.EmberEvent;
import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.api.tile.IBin;
import com.rekindled.embers.api.tile.IExtraCapabilityInformation;
import com.rekindled.embers.api.tile.IExtraDialInformation;
import com.rekindled.embers.api.upgrades.UpgradeContext;
import com.rekindled.embers.api.upgrades.UpgradeUtil;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.particle.SmokeParticleOptions;
import com.rekindled.embers.power.DefaultEmberCapability;
import com.rekindled.embers.util.sound.ISoundController;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class CinderPlinthBlockEntity extends BlockEntity implements ISoundController, IExtraDialInformation, IExtraCapabilityInformation {

	public static double EMBER_COST = 0.5;
	public static int PROCESS_TIME = 40;
	int angle = 0;
	int progress = 0;
	public IEmberCapability capability = new DefaultEmberCapability() {
		@Override
		public void onContentsChanged() {
			super.onContentsChanged();
			CinderPlinthBlockEntity.this.setChanged();
		}
	};
	public ItemStackHandler inventory = new ItemStackHandler(1) {
		@Override
		protected void onContentsChanged(int slot) {
			CinderPlinthBlockEntity.this.setChanged();
		}
	};
	public LazyOptional<IItemHandler> holder = LazyOptional.of(() -> inventory);
	static Random random = new Random();
	protected List<UpgradeContext> upgrades = new ArrayList<>();

	public static final int SOUND_PROCESS = 1;
	public static final int[] SOUND_IDS = new int[]{SOUND_PROCESS};

	HashSet<Integer> soundsPlaying = new HashSet<>();

	public CinderPlinthBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.CINDER_PLINTH_ENTITY.get(), pPos, pBlockState);
		capability.setEmberCapacity(4000);
	}

	@Override
	public AABB getRenderBoundingBox() {
		return new AABB(worldPosition.offset(0, 0, 0), worldPosition.offset(1, 2, 1));
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		capability.deserializeNBT(nbt);
		progress = nbt.getInt("progress");
		inventory.deserializeNBT(nbt.getCompound("inventory"));
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		capability.writeToNBT(nbt);
		nbt.putInt("progress", progress);
		nbt.put("inventory", inventory.serializeNBT());
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

	public static void clientTick(Level level, BlockPos pos, BlockState state, CinderPlinthBlockEntity blockEntity) {
		blockEntity.angle++;
		blockEntity.handleSound();
		blockEntity.upgrades = UpgradeUtil.getUpgrades(level, pos, Direction.values());
		UpgradeUtil.verifyUpgrades(blockEntity, blockEntity.upgrades);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, CinderPlinthBlockEntity blockEntity) {
		blockEntity.upgrades = UpgradeUtil.getUpgrades(level, pos, Direction.values());
		UpgradeUtil.verifyUpgrades(blockEntity, blockEntity.upgrades);
		if (UpgradeUtil.doTick(blockEntity, blockEntity.upgrades))
			return;

		if (blockEntity.shouldWork()) {
			boolean cancel = UpgradeUtil.doWork(blockEntity, blockEntity.upgrades);
			if (!cancel) {
				blockEntity.progress++;
				((ServerLevel) level).sendParticles(new SmokeParticleOptions(SmokeParticleOptions.SMOKE_COLOR, 3.0f + random.nextFloat() * 0.4f), pos.getX() + 0.5f, pos.getY() + 0.875f, pos.getZ() + 0.5f, 1, 0.0125, 0.025, 0.0125, 1.0);
				double emberCost = UpgradeUtil.getTotalEmberConsumption(blockEntity, EMBER_COST, blockEntity.upgrades);
				UpgradeUtil.throwEvent(blockEntity, new EmberEvent(blockEntity, EmberEvent.EnumType.CONSUME, emberCost), blockEntity.upgrades);
				blockEntity.capability.removeAmount(emberCost, true);
				if (blockEntity.progress > UpgradeUtil.getWorkTime(blockEntity, PROCESS_TIME, blockEntity.upgrades)) {
					blockEntity.progress = 0;
					BlockEntity tile = level.getBlockEntity(pos.below());
					List<ItemStack> outputs = Lists.newArrayList(new ItemStack(RegistryManager.ASH.get(), 1));
					UpgradeUtil.transformOutput(blockEntity, outputs, blockEntity.upgrades);
					blockEntity.inventory.extractItem(0, 1, false);
					for (ItemStack remainder : outputs) {
						if (tile instanceof IBin) {
							remainder = ((IBin) tile).getInventory().insertItem(0, remainder, false);
						}
						if (!remainder.isEmpty()) {
							level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, remainder));
						}
					}
					((ServerLevel) level).sendParticles(new SmokeParticleOptions(SmokeParticleOptions.SMOKE_COLOR, 3.0f), pos.getX() + 0.5f, pos.getY() + 1.1f, pos.getZ() + 0.5f, 9, 0.0125, 0.025, 0.0125, 1.0);
				}
			}
		} else {
			if (blockEntity.progress != 0) {
				blockEntity.progress = 0;
				blockEntity.setChanged();
			}
		}
	}

	private boolean shouldWork() {
		return !inventory.getStackInSlot(0).isEmpty() && capability.getEmber() > 0;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (!this.remove) {
			if (cap == EmbersCapabilities.EMBER_CAPABILITY) {
				return capability.getCapability(cap, side);
			}
			if (cap == ForgeCapabilities.ITEM_HANDLER) {
				return holder.cast();
			}
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		capability.invalidate();
		holder.invalidate();
	}

	@Override
	public void setChanged() {
		super.setChanged();
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
			EmbersSounds.playMachineSound(this, SOUND_PROCESS, EmbersSounds.PLINTH_LOOP.get(), SoundSource.BLOCKS, true, 1.0f, 1.0f, (float) worldPosition.getX() + 0.5f, (float) worldPosition.getY() + 0.5f, (float) worldPosition.getZ() + 0.5f);
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
		return id == SOUND_PROCESS && shouldWork();
	}

	@Override
	public void addDialInformation(Direction facing, List<String> information, String dialType) {
		UpgradeUtil.throwEvent(this, new DialInformationEvent(this, information, dialType), upgrades);
	}

	@Override
	public boolean hasCapabilityDescription(Capability<?> capability) {
		return capability == ForgeCapabilities.ITEM_HANDLER;
	}

	@Override
	public void addCapabilityDescription(List<String> strings, Capability<?> capability, Direction facing) {
		if(capability == ForgeCapabilities.ITEM_HANDLER) {
			strings.add(IExtraCapabilityInformation.formatCapability(EnumIOType.INPUT, Embers.MODID + ".tooltip.goggles.item", null));
			strings.add(IExtraCapabilityInformation.formatCapability(EnumIOType.OUTPUT, Embers.MODID + ".tooltip.goggles.item", I18n.get(Embers.MODID + ".tooltip.goggles.item.ash")));
		}
	}
}
