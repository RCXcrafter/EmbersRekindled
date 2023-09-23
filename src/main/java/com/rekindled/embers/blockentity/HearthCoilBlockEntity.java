package com.rekindled.embers.blockentity;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.joml.Vector3f;

import com.google.common.collect.Lists;
import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.event.DialInformationEvent;
import com.rekindled.embers.api.event.EmberEvent;
import com.rekindled.embers.api.event.HeatCoilVisualEvent;
import com.rekindled.embers.api.event.MachineRecipeEvent;
import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.api.tile.IExtraCapabilityInformation;
import com.rekindled.embers.api.tile.IExtraDialInformation;
import com.rekindled.embers.api.upgrades.UpgradeContext;
import com.rekindled.embers.api.upgrades.UpgradeUtil;
import com.rekindled.embers.block.EmberDialBlock;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.particle.GlowParticleOptions;
import com.rekindled.embers.particle.SmokeParticleOptions;
import com.rekindled.embers.power.DefaultEmberCapability;
import com.rekindled.embers.recipe.SingleItemContainer;
import com.rekindled.embers.util.DecimalFormats;
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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class HearthCoilBlockEntity extends BlockEntity implements ISoundController, IExtraDialInformation, IExtraCapabilityInformation {

	public static final double EMBER_COST = 1.0;
	public static final double HEATING_SPEED = 1.0;
	public static final double COOLING_SPEED = 1.0;
	public static final double MAX_HEAT = 280;
	public static final int MIN_COOK_TIME = 20;
	public static final int MAX_COOK_TIME = 300;
	public static final Color DEFAULT_COLOR = new Color(255, 64, 16);

	public IEmberCapability capability = new DefaultEmberCapability() {
		@Override
		public void onContentsChanged() {
			super.onContentsChanged();
			HearthCoilBlockEntity.this.setChanged();
		}
	};
	public ItemStackHandler inventory = new ItemStackHandler(1) {
		@Override
		public void onContentsChanged(int slot) {
			super.onContentsChanged(slot);
			HearthCoilBlockEntity.this.setChanged();
		}
	};
	public LazyOptional<IItemHandler> holder = LazyOptional.of(() -> inventory);
	protected static Random random = new Random();
	protected int progress = 0;
	public double heat = 0;
	protected int ticksExisted = 0;

	public static final int SOUND_LOW_LOOP = 1;
	public static final int SOUND_MID_LOOP = 2;
	public static final int SOUND_HIGH_LOOP = 3;
	public static final int SOUND_PROCESS = 4;
	public static final int[] SOUND_IDS = new int[]{SOUND_LOW_LOOP, SOUND_MID_LOOP, SOUND_HIGH_LOOP, SOUND_PROCESS};

	HashSet<Integer> soundsPlaying = new HashSet<>();
	boolean isWorking;
	protected List<UpgradeContext> upgrades;
	public SmeltingRecipe cachedRecipe = null;

	public HearthCoilBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.HEARTH_COIL_ENTITY.get(), pPos, pBlockState);
		capability.setEmberCapacity(8000);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		capability.deserializeNBT(nbt);
		inventory.deserializeNBT(nbt.getCompound("inventory"));
		progress = nbt.getInt("progress");
		heat = nbt.getDouble("heat");
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		capability.writeToNBT(nbt);
		nbt.put("inventory", inventory.serializeNBT());
		nbt.putInt("progress", progress);
		nbt.putDouble("heat", heat);
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
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (!this.remove && (side == null || side == Direction.DOWN)) {
			if (cap == EmbersCapabilities.EMBER_CAPABILITY) {
				return capability.getCapability(cap, side);
			} else if (cap == ForgeCapabilities.ITEM_HANDLER) {
				return ForgeCapabilities.ITEM_HANDLER.orEmpty(cap, holder);
			}
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
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

	public static void serverTick(Level level, BlockPos pos, BlockState state, HearthCoilBlockEntity blockEntity) {
		blockEntity.ticksExisted++;

		blockEntity.upgrades = UpgradeUtil.getUpgrades(level, pos, new Direction[]{Direction.DOWN});
		UpgradeUtil.verifyUpgrades(blockEntity, blockEntity.upgrades);
		if (UpgradeUtil.doTick(blockEntity, blockEntity.upgrades))
			return;

		double emberCost = UpgradeUtil.getTotalEmberConsumption(blockEntity, EMBER_COST, blockEntity.upgrades);
		double prevHeat = blockEntity.heat;
		Boolean cancel = null;
		if (blockEntity.capability.getEmber() >= emberCost) {
			cancel = UpgradeUtil.doWork(blockEntity, blockEntity.upgrades);
			if (!cancel) {
				UpgradeUtil.throwEvent(blockEntity, new EmberEvent(blockEntity, EmberEvent.EnumType.CONSUME, emberCost), blockEntity.upgrades);
				blockEntity.capability.removeAmount(emberCost, true);
				if (blockEntity.ticksExisted % 20 == 0) {
					blockEntity.heat += UpgradeUtil.getOtherParameter(blockEntity, "heating_speed", HEATING_SPEED, blockEntity.upgrades);
				}
			} else {
				if (blockEntity.ticksExisted % 20 == 0) {
					blockEntity.heat -= UpgradeUtil.getOtherParameter(blockEntity, "cooling_speed", COOLING_SPEED, blockEntity.upgrades);
				}
			}
		} else {
			if (blockEntity.ticksExisted % 20 == 0) {
				blockEntity.heat -= UpgradeUtil.getOtherParameter(blockEntity, "cooling_speed", COOLING_SPEED, blockEntity.upgrades);
			}
		}
		double maxHeat = UpgradeUtil.getOtherParameter(blockEntity, "max_heat", MAX_HEAT, blockEntity.upgrades);
		blockEntity.heat = Mth.clamp(blockEntity.heat, 0, maxHeat);
		blockEntity.isWorking = false;
		if (blockEntity.heat != prevHeat)
			blockEntity.setChanged();

		int cookTime = UpgradeUtil.getWorkTime(blockEntity,(int)Math.ceil(Mth.clampedLerp(MIN_COOK_TIME,MAX_COOK_TIME, 1.0 - (blockEntity.heat / maxHeat))), blockEntity.upgrades);
		if (blockEntity.heat > 0 && blockEntity.ticksExisted % cookTime == 0) {
			if (cancel == null)
				cancel = UpgradeUtil.doWork(blockEntity, blockEntity.upgrades);
			if (!cancel) {
				List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, new AABB(pos.getX()-1, pos.getY(), pos.getZ()-1, pos.getX()+2, pos.getY()+2, pos.getZ()+2));
				for (ItemEntity item : items) {
					item.setUnlimitedLifetime();
					item.lifespan = 10800;
				}
				if (items.size() > 0) {
					int i = random.nextInt(items.size());
					ItemEntity entityItem = items.get(i);
					SingleItemContainer wrapper = new SingleItemContainer(entityItem.getItem());
					blockEntity.cachedRecipe = Misc.getRecipe(blockEntity.cachedRecipe, RecipeType.SMELTING, wrapper, level);

					if (blockEntity.cachedRecipe != null) {
						ArrayList<ItemStack> returns = Lists.newArrayList(blockEntity.cachedRecipe.assemble(wrapper, level.registryAccess()));
						//int inputCount = recipe.getInputConsumed();
						UpgradeUtil.throwEvent(blockEntity, new MachineRecipeEvent.Success<>(blockEntity, blockEntity.cachedRecipe), blockEntity.upgrades);
						UpgradeUtil.transformOutput(blockEntity, returns, blockEntity.upgrades);
						depleteItem(entityItem, 1);
						for(ItemStack stack : returns) {
							ItemStack remainder = blockEntity.inventory.insertItem(0, stack, false);
							if (!remainder.isEmpty())
								level.addFreshEntity(new ItemEntity(level, entityItem.getX(), entityItem.getY(), entityItem.getZ(), remainder));
						}
					}
				}
			}
		}
	}

	public static void clientTick(Level level, BlockPos pos, BlockState state, HearthCoilBlockEntity blockEntity) {
		blockEntity.handleSound();

		blockEntity.upgrades = UpgradeUtil.getUpgrades(level, pos, new Direction[]{Direction.DOWN});
		UpgradeUtil.verifyUpgrades(blockEntity, blockEntity.upgrades);
		if (UpgradeUtil.doTick(blockEntity, blockEntity.upgrades))
			return;

		if (blockEntity.heat > 0) {
			int particleCount = (int) ((1 + random.nextInt(2)) * (1 + (float) Math.sqrt(blockEntity.heat)));
			HeatCoilVisualEvent event = new HeatCoilVisualEvent(blockEntity, DEFAULT_COLOR, particleCount, 0);
			UpgradeUtil.throwEvent(blockEntity, event, blockEntity.upgrades);
			Color color = event.getColor();
			GlowParticleOptions options = new GlowParticleOptions(new Vector3f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F), 2.0F);
			for (int i = 0; i < event.getParticles(); i ++) {
				level.addParticle(options, pos.getX()-0.2f+random.nextFloat()*1.4f, pos.getY()+1.275f, pos.getZ()-0.2f+random.nextFloat()*1.4f,
						(Math.random() * 2.0D - 1.0D) * 0.2D, random.nextFloat() * event.getVerticalSpeed(), (Math.random() * 2.0D - 1.0D) * 0.2D);
			}
		}
	}

	/*private HeatCoilRecipe getRecipe(ItemEntity entityItem) {
		HeatCoilRecipe recipe = RecipeRegistry.getHeatCoilRecipe(entityItem.getItem());
		MachineRecipeEvent<HeatCoilRecipe> event = new MachineRecipeEvent<>(this, recipe);
		UpgradeUtil.throwEvent(this, event,upgrades);
		return event.getRecipe();
	}*/

	public static void depleteItem(ItemEntity entityItem, int inputCount) {
		ItemStack stack = entityItem.getItem();
		stack.shrink(inputCount);
		entityItem.setItem(stack);
		((ServerLevel) entityItem.level()).sendParticles(new SmokeParticleOptions(SmokeParticleOptions.SMOKE_COLOR, 5.0f), entityItem.getX(), entityItem.getY(), entityItem.getZ(), 2, 0.07, 0.07, 0.07, 1.0);
		((ServerLevel) entityItem.level()).sendParticles(new SmokeParticleOptions(SmokeParticleOptions.SMOKE_COLOR, 2.0f), entityItem.getX(), entityItem.getY(), entityItem.getZ(), 3, 0.07, 0.07, 0.07, 1.0);
		if (stack.isEmpty()) {
			entityItem.discard();
		}
	}

	@Override
	public void playSound(int id) {
		float soundX = (float) worldPosition.getX() + 0.5f;
		float soundY = (float) worldPosition.getY() - 0.5f;
		float soundZ = (float) worldPosition.getZ() + 0.5f;
		switch (id) {
		case SOUND_LOW_LOOP:
			EmbersSounds.playMachineSound(this, SOUND_LOW_LOOP, EmbersSounds.HEATCOIL_LOW.get(), SoundSource.BLOCKS, true, 1.0f, 1.0f, soundX, soundY, soundZ);
			break;
		case SOUND_MID_LOOP:
			EmbersSounds.playMachineSound(this, SOUND_MID_LOOP, EmbersSounds.HEATCOIL_MID.get(), SoundSource.BLOCKS, true, 1.0f, 1.0f, soundX, soundY, soundZ);
			break;
		case SOUND_HIGH_LOOP:
			EmbersSounds.playMachineSound(this, SOUND_HIGH_LOOP, EmbersSounds.HEATCOIL_HIGH.get(), SoundSource.BLOCKS, true, 1.0f, 1.0f, soundX, soundY, soundZ);
			break;
		case SOUND_PROCESS:
			EmbersSounds.playMachineSound(this, SOUND_PROCESS, EmbersSounds.HEATCOIL_COOK.get(), SoundSource.BLOCKS, true, 1.0f, 1.0f, soundX, soundY, soundZ);
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
		double heatRatio = heat / MAX_HEAT;
		float highVolume = (float)Mth.clampedLerp(0,1,(heatRatio -0.75) * 4);
		float midVolume = (float)Mth.clampedLerp(0,1,(heatRatio -0.25) * 4) - highVolume;
		float lowVolume = (float)Mth.clampedLerp(0,1, heatRatio * 10) - midVolume;

		switch (id) {
		case SOUND_LOW_LOOP: return lowVolume > 0;
		case SOUND_MID_LOOP: return midVolume > 0;
		case SOUND_HIGH_LOOP: return highVolume > 0;
		default: return false;
		}
	}

	@Override
	public float getCurrentVolume(int id, float volume) {
		double heatRatio = heat / MAX_HEAT;
		float highVolume = (float)Mth.clampedLerp(0,1,(heatRatio -0.75) * 4);
		float midVolume = (float)Mth.clampedLerp(0,1,(heatRatio -0.25) * 4) - highVolume;
		float lowVolume = (float)Mth.clampedLerp(0,1, heatRatio * 10) - midVolume;

		switch (id) {
		case SOUND_LOW_LOOP: return lowVolume;
		case SOUND_MID_LOOP: return midVolume;
		case SOUND_HIGH_LOOP: return highVolume;
		default: return 0.0f;
		}
	}

	@Override
	public void addDialInformation(Direction facing, List<String> information, String dialType) {
		if (EmberDialBlock.DIAL_TYPE.equals(dialType)) {
			DecimalFormat heatFormat = DecimalFormats.getDecimalFormat(Embers.MODID + ".decimal_format.heat");
			double maxHeat = UpgradeUtil.getOtherParameter(this, "max_heat", MAX_HEAT, upgrades);
			double heat = Mth.clamp(this.heat, 0, maxHeat);
			information.add(I18n.get(Embers.MODID + ".tooltip.dial.heat", heatFormat.format(heat), heatFormat.format(maxHeat)));
		}
		UpgradeUtil.throwEvent(this, new DialInformationEvent(this, information, dialType), upgrades);
	}

	@Override
	public boolean hasCapabilityDescription(Capability<?> capability) {
		return capability == ForgeCapabilities.ITEM_HANDLER;
	}

	@Override
	public void addCapabilityDescription(List<String> strings, Capability<?> capability, Direction facing) {
		if (capability == ForgeCapabilities.ITEM_HANDLER)
			strings.add(IExtraCapabilityInformation.formatCapability(EnumIOType.OUTPUT, Embers.MODID + ".tooltip.goggles.item", null));
	}
}
