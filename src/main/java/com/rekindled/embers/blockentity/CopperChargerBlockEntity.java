package com.rekindled.embers.blockentity;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.jetbrains.annotations.NotNull;

import com.rekindled.embers.ConfigManager;
import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.event.DialInformationEvent;
import com.rekindled.embers.api.event.EmberEvent;
import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.api.tile.IExtraCapabilityInformation;
import com.rekindled.embers.api.tile.IExtraDialInformation;
import com.rekindled.embers.api.upgrades.UpgradeContext;
import com.rekindled.embers.api.upgrades.UpgradeUtil;
import com.rekindled.embers.block.EmberDialBlock;
import com.rekindled.embers.block.ItemDialBlock;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.particle.GlowParticleOptions;
import com.rekindled.embers.power.DefaultEmberCapability;
import com.rekindled.embers.util.sound.ISoundController;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class CopperChargerBlockEntity extends BlockEntity implements ISoundController, IExtraDialInformation, IExtraCapabilityInformation {

	public IEmberCapability capability = new DefaultEmberCapability() {
		@Override
		public void onContentsChanged() {
			super.onContentsChanged();
			CopperChargerBlockEntity.this.setChanged();
		}
	};

	public ItemStackHandler inventory = new ItemStackHandler(1) {
		@Override
		public int getSlotLimit(int slot) {
			return 1;
		}

		@Override
		public boolean isItemValid(int slot, @NotNull ItemStack stack) {
			return stack.getCapability(EmbersCapabilities.EMBER_CAPABILITY, null).isPresent();
		}

		@Override
		protected void onContentsChanged(int slot) {
			CopperChargerBlockEntity.this.setChanged();
		}
	};
	public LazyOptional<IItemHandler> holder = LazyOptional.of(() -> inventory);

	public int angle = 0;
	public int turnRate = 1;
	static Random random = new Random();
	public boolean isWorking;
	public boolean wasWorking;
	public boolean reverse = false;
	public boolean dirty = false;
	protected List<UpgradeContext> upgrades;

	public static final int SOUND_PROCESS = 1;
	public static final int SOUND_REVERSE = 2;
	public static final int[] SOUND_IDS = new int[]{SOUND_PROCESS, SOUND_REVERSE};

	HashSet<Integer> soundsPlaying = new HashSet<>();

	public CopperChargerBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.COPPER_CHARGER_ENTITY.get(), pPos, pBlockState);
		capability.setEmberCapacity(24000);
		capability.setEmber(0);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		capability.deserializeNBT(nbt);
		inventory.deserializeNBT(nbt.getCompound("inventory"));
		isWorking = nbt.getBoolean("working");
		reverse = nbt.getBoolean("reverse");
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		capability.writeToNBT(nbt);
		nbt.put("inventory", inventory.serializeNBT());
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag nbt = super.getUpdateTag();
		saveAdditional(nbt);
		nbt.putBoolean("working", isWorking);
		nbt.putBoolean("reverse", reverse);
		return nbt;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	public static void clientTick(Level level, BlockPos pos, BlockState state, CopperChargerBlockEntity blockEntity) {
		blockEntity.upgrades = UpgradeUtil.getUpgrades(level, pos, Direction.values());
		UpgradeUtil.verifyUpgrades(blockEntity, blockEntity.upgrades);
		blockEntity.handleSound();
		blockEntity.angle += blockEntity.turnRate;

		if (blockEntity.isWorking && blockEntity.capability.getEmber() > 0) {
			for (int i = 0; i < Math.ceil(blockEntity.capability.getEmber() / 500.0); i++) {
				level.addParticle(GlowParticleOptions.EMBER, pos.getX()+0.25f+random.nextFloat()*0.5f, pos.getY()+0.25f+random.nextFloat()*0.5f, pos.getZ()+0.25f+random.nextFloat()*0.5f,
						(Math.random() * 2.0D - 1.0D) * 0.2D, (Math.random() * 2.0D - 1.0D) * 0.2D, (Math.random() * 2.0D - 1.0D) * 0.2D);
			}
		}
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, CopperChargerBlockEntity blockEntity) {
		blockEntity.upgrades = UpgradeUtil.getUpgrades(level, pos, Direction.values());
		UpgradeUtil.verifyUpgrades(blockEntity, blockEntity.upgrades);
		if (UpgradeUtil.doTick(blockEntity, blockEntity.upgrades))
			return;

		ItemStack stack = blockEntity.inventory.getStackInSlot(0);
		blockEntity.wasWorking = blockEntity.isWorking;
		blockEntity.isWorking = false;

		IEmberCapability itemCapability = stack.getCapability(EmbersCapabilities.EMBER_CAPABILITY, null).orElse(null);
		boolean cancel = UpgradeUtil.doWork(blockEntity, blockEntity.upgrades);
		if (!cancel && itemCapability != null) {
			double transferRate = UpgradeUtil.getTotalSpeedModifier(blockEntity, blockEntity.upgrades) * ConfigManager.CHARGER_MAX_TRANSFER.get();
			double emberAdded;
			if (transferRate > 0) {
				emberAdded = itemCapability.addAmount(Math.min(Math.abs(transferRate), blockEntity.capability.getEmber()), true);
				blockEntity.capability.removeAmount(emberAdded, true);
				blockEntity.reverse = false;
			} else {
				emberAdded = blockEntity.capability.addAmount(Math.min(Math.abs(transferRate), itemCapability.getEmber()), true);
				itemCapability.removeAmount(emberAdded, true);
				blockEntity.reverse = true;
			}
			if (emberAdded > 0) {
				UpgradeUtil.throwEvent(blockEntity, new EmberEvent(blockEntity, EmberEvent.EnumType.TRANSFER, emberAdded), blockEntity.upgrades);
				blockEntity.isWorking = true;
			}
		}
		if (blockEntity.wasWorking != blockEntity.isWorking) {
			blockEntity.setChanged();
		}
		if (blockEntity.dirty) {
			for (ServerPlayer serverplayer : level.getServer().getPlayerList().getPlayers()) {
				serverplayer.connection.send(blockEntity.getUpdatePacket());
			}
		}
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (!this.remove) {
			if (cap == EmbersCapabilities.EMBER_CAPABILITY) {
				return capability.getCapability(cap, side);
			}
			if (cap == ForgeCapabilities.ITEM_HANDLER) {
				return ForgeCapabilities.ITEM_HANDLER.orEmpty(cap, holder);
			}
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void playSound(int id) {
		switch (id) {
		case SOUND_PROCESS:
			EmbersSounds.playMachineSound(this, SOUND_PROCESS, EmbersSounds.COPPER_CHARGER_LOOP.get(), SoundSource.BLOCKS, true, 1.0f, 1.0f, (float)worldPosition.getX()+0.5f,(float)worldPosition.getY()+0.5f,(float)worldPosition.getZ()+0.5f);
			break;
		case SOUND_REVERSE:
			EmbersSounds.playMachineSound(this, SOUND_REVERSE, EmbersSounds.COPPER_CHARGER_SIPHON_LOOP.get(), SoundSource.BLOCKS, true, 1.0f, 1.0f, (float)worldPosition.getX()+0.5f,(float)worldPosition.getY()+0.5f,(float)worldPosition.getZ()+0.5f);
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
		return isWorking && (reverse ? id == SOUND_REVERSE : id == SOUND_PROCESS);
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
		dirty = true;
	}

	@Override
	public void addDialInformation(Direction facing, List<String> information, String dialType) {
		if (EmberDialBlock.DIAL_TYPE.equals(dialType)) {
			ItemStack stack = inventory.getStackInSlot(0);
			IEmberCapability itemCapability = stack.getCapability(EmbersCapabilities.EMBER_CAPABILITY,null).orElse(null);
			if (itemCapability != null) {
				information.add(ItemDialBlock.formatItemStack(stack));
				information.add(EmberDialBlock.formatEmber(itemCapability.getEmber(),itemCapability.getEmberCapacity()));
			}
		}
		UpgradeUtil.throwEvent(this, new DialInformationEvent(this, information, dialType), upgrades);
	}

	@Override
	public boolean hasCapabilityDescription(Capability<?> capability) {
		return capability == ForgeCapabilities.ITEM_HANDLER;
	}

	@Override
	public void addCapabilityDescription(List<String> strings, Capability<?> capability, Direction facing) {
		if(capability == ForgeCapabilities.ITEM_HANDLER)
			strings.add(IExtraCapabilityInformation.formatCapability(EnumIOType.BOTH, Embers.MODID + ".tooltip.goggles.item", I18n.get(Embers.MODID + ".tooltip.goggles.item.ember_storage")));
	}
}
