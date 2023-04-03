package com.rekindled.embers.blockentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.event.DialInformationEvent;
import com.rekindled.embers.api.event.EmberEvent;
import com.rekindled.embers.api.event.MachineRecipeEvent;
import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.api.tile.IBin;
import com.rekindled.embers.api.tile.IExtraCapabilityInformation;
import com.rekindled.embers.api.tile.IExtraDialInformation;
import com.rekindled.embers.api.tile.IMechanicallyPowered;
import com.rekindled.embers.api.upgrades.IUpgradeProvider;
import com.rekindled.embers.api.upgrades.UpgradeUtil;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.particle.GlowParticleOptions;
import com.rekindled.embers.particle.SmokeParticleOptions;
import com.rekindled.embers.particle.SparkParticleOptions;
import com.rekindled.embers.power.DefaultEmberCapability;
import com.rekindled.embers.recipe.StampingContext;
import com.rekindled.embers.recipe.StampingRecipe;
import com.rekindled.embers.util.Misc;

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
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class StamperBlockEntity extends BlockEntity implements IMechanicallyPowered, IExtraDialInformation, IExtraCapabilityInformation {

	public static final double EMBER_COST = 80.0;
	public static final int STAMP_TIME = 70;
	public static final int RETRACT_TIME = 10;
	public IEmberCapability capability = new DefaultEmberCapability() {
		@Override
		public void onContentsChanged() {
			super.onContentsChanged();
			StamperBlockEntity.this.setChanged();
		}
	};
	public boolean prevPowered = false;
	public boolean powered = false;
	public long ticksExisted = 0;
	Random random = new Random();
	public ItemStackHandler stamp = new ItemStackHandler(1) {
		@Override
		protected void onContentsChanged(int slot) {
			StamperBlockEntity.this.setChanged();
		}

		@Override
		public int getSlotLimit(int slot) {
			return 1;
		}
	};
	public LazyOptional<IItemHandler> holder = LazyOptional.of(() -> stamp);
	private List<IUpgradeProvider> upgrades = new ArrayList<>();
	public StampingRecipe cachedRecipe = null;

	public StamperBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.STAMPER_ENTITY.get(), pPos, pBlockState);
		capability.setEmberCapacity(8000);
	}

	@Override
	public AABB getRenderBoundingBox() {
		return new AABB(worldPosition.offset(0, -1, 0), worldPosition.offset(1, 1, 1));
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		capability.deserializeNBT(nbt);
		powered = nbt.getBoolean("powered");
		stamp.deserializeNBT(nbt.getCompound("stamp"));
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		capability.writeToNBT(nbt);
		nbt.putBoolean("powered", powered);
		nbt.put("stamp", stamp.serializeNBT());
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

	public static void clientTick(Level level, BlockPos pos, BlockState state, StamperBlockEntity blockEntity) {
		blockEntity.prevPowered = blockEntity.powered;
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, StamperBlockEntity blockEntity) {
		blockEntity.ticksExisted++;
		blockEntity.prevPowered = blockEntity.powered;
		if (level.getBlockState(pos.below(2)).getBlock() == RegistryManager.STAMP_BASE.get()) {
			blockEntity.upgrades = UpgradeUtil.getUpgrades(level, pos, Misc.horizontals);
			UpgradeUtil.verifyUpgrades(blockEntity, blockEntity.upgrades);
			if (UpgradeUtil.doTick(blockEntity, blockEntity.upgrades))
				return;

			StampBaseBlockEntity stamp = (StampBaseBlockEntity) level.getBlockEntity(pos.below(2));
			IFluidHandler handler = stamp.getTank();

			StampingContext context = new StampingContext(stamp.inventory, handler, blockEntity.stamp.getStackInSlot(0));
			blockEntity.cachedRecipe = Misc.getRecipe(blockEntity.cachedRecipe, RegistryManager.STAMPING.get(), context, level);

			if (blockEntity.cachedRecipe != null || blockEntity.powered) {
				boolean cancel = UpgradeUtil.doWork(blockEntity, blockEntity.upgrades);
				int stampTime = UpgradeUtil.getWorkTime(blockEntity, STAMP_TIME, blockEntity.upgrades);
				int retractTime = UpgradeUtil.getWorkTime(blockEntity, RETRACT_TIME, blockEntity.upgrades);
				if (!cancel && !blockEntity.powered && blockEntity.ticksExisted >= stampTime) {
					double emberCost = UpgradeUtil.getTotalEmberConsumption(blockEntity, EMBER_COST, blockEntity.upgrades);
					if (blockEntity.capability.getEmber() >= emberCost) {
						UpgradeUtil.throwEvent(blockEntity, new EmberEvent(blockEntity, EmberEvent.EnumType.CONSUME, emberCost), blockEntity.upgrades);
						blockEntity.capability.removeAmount(emberCost, true);
						if (level instanceof ServerLevel serverLevel) {
							serverLevel.sendParticles(new SparkParticleOptions(GlowParticleOptions.EMBER_COLOR, 1.0f), pos.getX() + 0.5f, pos.getY() - 1.1f, pos.getZ() + 0.5f, 10, 0.25, 0.0, 0.25, 1.0);
							serverLevel.sendParticles(new SmokeParticleOptions(SmokeParticleOptions.SMOKE_COLOR, 3.0f), pos.getX() + 0.5f, pos.getY() - 1.1f, pos.getZ() + 0.5f, 10, 0.25, 0.0, 0.25, 1.0);
						}

						level.playSound(null, pos.below(), EmbersSounds.STAMPER_DOWN.get(), SoundSource.BLOCKS, 1.0f, 1.0f);

						blockEntity.powered = true;
						blockEntity.ticksExisted = 0;

						UpgradeUtil.throwEvent(blockEntity, new MachineRecipeEvent.Success<>(blockEntity, blockEntity.cachedRecipe), blockEntity.upgrades);

						//the recipe is responsible for taking items and fluid from the inventory
						List<ItemStack> results = Lists.newArrayList(blockEntity.cachedRecipe.assemble(context).copy());
						UpgradeUtil.transformOutput(blockEntity, results, blockEntity.upgrades);

						BlockPos middlePos = pos.below();
						BlockPos outputPos = pos.below(3);
						BlockEntity outputTile = level.getBlockEntity(outputPos);
						for (ItemStack remainder : results) {
							if (outputTile instanceof IBin) {
								remainder = ((IBin) outputTile).getInventory().insertItem(0, remainder, false);
							}
							if (!remainder.isEmpty()) {
								level.addFreshEntity(new ItemEntity(level, middlePos.getX() + 0.5, middlePos.getY() + 0.5, middlePos.getZ() + 0.5, remainder));
							}
						}
						stamp.setChanged();
					}

					blockEntity.setChanged();
				} else if (!cancel && blockEntity.powered && blockEntity.ticksExisted >= retractTime) {
					blockEntity.retract();
				}
			}
		} else if (blockEntity.powered) {
			blockEntity.retract();
		}
	}

	private void retract() {
		level.playSound(null, worldPosition.below(), EmbersSounds.STAMPER_UP.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
		powered = false;
		ticksExisted = 0;
		setChanged();
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (!this.remove && cap == EmbersCapabilities.EMBER_CAPABILITY) {
			return capability.getCapability(cap, side);
		}
		if (!this.remove && cap == ForgeCapabilities.ITEM_HANDLER) {
			return holder.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		capability.invalidate();
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
	public double getMechanicalSpeed(double power) {
		return Misc.getDiminishedPower(power,20,1.5/20);
	}

	@Override
	public double getNominalSpeed() {
		return 1;
	}

	@Override
	public double getMinimumPower() {
		return 10;
	}

	@Override
	public void addDialInformation(Direction facing, List<String> information, String dialType) {
		UpgradeUtil.throwEvent(this,new DialInformationEvent(this,information,dialType),upgrades);
	}

	@Override
	public boolean hasCapabilityDescription(Capability<?> capability) {
		return capability == ForgeCapabilities.ITEM_HANDLER;
	}

	@Override
	public void addCapabilityDescription(List<String> strings, Capability<?> capability, Direction facing) {
		if(capability == ForgeCapabilities.ITEM_HANDLER)
			strings.add(IExtraCapabilityInformation.formatCapability(EnumIOType.BOTH, Embers.MODID + ".tooltip.goggles.item", I18n.get(Embers.MODID + ".tooltip.goggles.item.stamp")));
	}
}
