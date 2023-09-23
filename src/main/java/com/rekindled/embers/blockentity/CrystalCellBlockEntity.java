package com.rekindled.embers.blockentity;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.event.DialInformationEvent;
import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.api.tile.IExtraCapabilityInformation;
import com.rekindled.embers.api.tile.IExtraDialInformation;
import com.rekindled.embers.api.upgrades.UpgradeContext;
import com.rekindled.embers.api.upgrades.UpgradeUtil;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.particle.GlowParticleOptions;
import com.rekindled.embers.power.DefaultEmberCapability;
import com.rekindled.embers.recipe.EmberActivationRecipe;
import com.rekindled.embers.recipe.SingleItemContainer;
import com.rekindled.embers.util.Misc;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class CrystalCellBlockEntity extends BlockEntity implements ISoundController, IExtraDialInformation, IExtraCapabilityInformation {
	public static final int MAX_CAPACITY = 1440000;
	Random random = new Random();
	public long ticksExisted = 0;
	public float angle = 0;
	public long seed = 0;
	public double renderCapacity;
	public double renderCapacityLast;
	public IEmberCapability capability = new DefaultEmberCapability() {
		@Override
		public void onContentsChanged() {
			super.onContentsChanged();
			CrystalCellBlockEntity.this.setChanged();
		}

		@Override
		public boolean acceptsVolatile() {
			return true;
		}
	};
	public ItemStackHandler inventory = new ItemStackHandler(1) {
		@Override
		protected void onContentsChanged(int slot) {
			CrystalCellBlockEntity.this.setChanged();
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (Misc.getRecipe(cachedRecipe, RegistryManager.EMBER_ACTIVATION.get(), new SingleItemContainer(stack), level) != null) {
				return super.insertItem(slot, stack, simulate);
			}
			return stack;
		}
	};
	public LazyOptional<IItemHandler> holder = LazyOptional.of(() -> inventory);
	public EmberActivationRecipe cachedRecipe = null;
	protected List<UpgradeContext> upgrades;

	public static final int SOUND_AMBIENT = 1;
	public static final int[] SOUND_IDS = new int[]{SOUND_AMBIENT};

	HashSet<Integer> soundsPlaying = new HashSet<>();

	public CrystalCellBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.CRYSTAL_CELL_ENTITY.get(), pPos, pBlockState);
		capability.setEmberCapacity(64000);
		seed = random.nextLong();
	}

	@Override
	public AABB getRenderBoundingBox() {
		return new AABB(worldPosition.offset(-1, 1, -1), worldPosition.offset(2, 5, 2));
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		seed = nbt.getLong("seed");
		inventory.deserializeNBT(nbt.getCompound("inventory"));
		capability.deserializeNBT(nbt);
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		nbt.putLong("seed", seed);
		nbt.put("inventory", inventory.serializeNBT());
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

	public static void commonTick(Level level, BlockPos pos, BlockState state, CrystalCellBlockEntity blockEntity) {
		blockEntity.upgrades = UpgradeUtil.getUpgrades(level, pos, new Direction[]{Direction.DOWN});
		UpgradeUtil.verifyUpgrades(blockEntity, blockEntity.upgrades);
		if (UpgradeUtil.doTick(blockEntity, blockEntity.upgrades))
			return;
		if (level.isClientSide)
			blockEntity.handleSound();
		blockEntity.ticksExisted++;
		blockEntity.renderCapacityLast = blockEntity.renderCapacity;
		if (blockEntity.renderCapacity < blockEntity.capability.getEmberCapacity())
			blockEntity.renderCapacity += Math.min(10000, blockEntity.capability.getEmberCapacity() - blockEntity.renderCapacity);
		else
			blockEntity.renderCapacity -= Math.min(10000, blockEntity.renderCapacity - blockEntity.capability.getEmberCapacity());
		if (!blockEntity.inventory.getStackInSlot(0).isEmpty() && blockEntity.ticksExisted % 4 == 0) {
			boolean cancel = UpgradeUtil.doWork(blockEntity, blockEntity.upgrades);
			if (!cancel) {
				RecipeWrapper wrapper = new RecipeWrapper(blockEntity.inventory);
				blockEntity.cachedRecipe = Misc.getRecipe(blockEntity.cachedRecipe, RegistryManager.EMBER_ACTIVATION.get(), wrapper, level);
				if (blockEntity.cachedRecipe != null) {
					double emberValue = blockEntity.cachedRecipe.process(wrapper);
					if (!level.isClientSide) {
						blockEntity.inventory.extractItem(0, 1, false);
						int maxCapacity = UpgradeUtil.getOtherParameter(blockEntity, "max_capacity", MAX_CAPACITY, blockEntity.upgrades);
						if (blockEntity.capability.getEmberCapacity() < maxCapacity) {
							blockEntity.capability.setEmberCapacity(Math.min(maxCapacity, blockEntity.capability.getEmberCapacity() + emberValue * 10));
							blockEntity.setChanged();
						}
					} else {
						double angle = blockEntity.random.nextDouble() * 2.0 * Math.PI;
						double x = pos.getX() + 0.5 + 0.5 * Math.sin(angle);
						double z = pos.getZ() + 0.5 + 0.5 * Math.cos(angle);
						double x2 = pos.getX() + 0.5;
						double z2 = pos.getZ() + 0.5;
						float layerHeight = 0.25f;
						float numLayers = 2 + (float) Math.floor(blockEntity.capability.getEmberCapacity() / 120000.0f);
						float height = layerHeight * numLayers;
						for (float i = 0; i < 72; i++) {
							float coeff = i / 72.0f;
							level.addParticle(GlowParticleOptions.EMBER_NOMOTION, x * (1.0f - coeff) + x2 * coeff, pos.getY() + (1.0f - coeff) + (height / 2.0f + 1.5f) * coeff, z * (1.0f - coeff) + z2 * coeff, 0, 0, 0);
						}
						level.playLocalSound(x, pos.getY() + 0.5, z, EmbersSounds.CRYSTAL_CELL_GROW.get(), SoundSource.BLOCKS, 1.0f, 1.0f + blockEntity.random.nextFloat(), false);
					}
				}
			}
		}
		float numLayers = 2 + (float) Math.floor(blockEntity.capability.getEmberCapacity() / 120000.0f);
		if (level.isClientSide) {
			for (int i = 0; i < numLayers / 2; i++) {
				float layerHeight = 0.25f;
				float height = layerHeight * numLayers;
				float xDest = pos.getX() + 0.5f;
				float yDest = pos.getY() + height / 2.0f + 1.5f;
				float zDest = pos.getZ() + 0.5f;
				float x = pos.getX() + 0.5f + 2.0f * (blockEntity.random.nextFloat() - 0.5f);
				float z = pos.getZ() + 0.5f + 2.0f * (blockEntity.random.nextFloat() - 0.5f);
				float y = pos.getY() + 1.0f;
				level.addParticle(new GlowParticleOptions(GlowParticleOptions.EMBER_COLOR, new Vec3((xDest - x) / 1.0f * blockEntity.random.nextFloat(), (yDest - y) / 1.0f * blockEntity.random.nextFloat(), (zDest - z) / 1.0f * blockEntity.random.nextFloat()), 2.0F), x, y, z, 0, 0, 0);
			}
		}
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (!this.remove && (side == null || side == Direction.DOWN)) {
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
		case SOUND_AMBIENT:
			EmbersSounds.playMachineSound(this, SOUND_AMBIENT, EmbersSounds.CRYSTAL_CELL_LOOP.get(), SoundSource.BLOCKS, true, 1.0f, 1.0f, worldPosition.getX() + 0.5f, worldPosition.getY() - 0.5f, worldPosition.getZ() + 0.5f);
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
		return id == SOUND_AMBIENT;
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
		if (capability == ForgeCapabilities.ITEM_HANDLER)
			strings.add(IExtraCapabilityInformation.formatCapability(EnumIOType.INPUT, Embers.MODID + ".tooltip.goggles.item", I18n.get(Embers.MODID + ".tooltip.goggles.item.ember")));
	}
}
