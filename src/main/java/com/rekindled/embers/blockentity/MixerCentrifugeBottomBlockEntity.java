package com.rekindled.embers.blockentity;

import java.util.HashSet;
import java.util.List;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.event.EmberEvent;
import com.rekindled.embers.api.event.MachineRecipeEvent;
import com.rekindled.embers.api.tile.IExtraCapabilityInformation;
import com.rekindled.embers.api.tile.IExtraDialInformation;
import com.rekindled.embers.api.tile.IMechanicallyPowered;
import com.rekindled.embers.api.upgrades.IUpgradeProvider;
import com.rekindled.embers.api.upgrades.UpgradeUtil;
import com.rekindled.embers.block.FluidDialBlock;
import com.rekindled.embers.datagen.EmbersFluidTags;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.recipe.MixingContext;
import com.rekindled.embers.recipe.MixingRecipe;
import com.rekindled.embers.util.FluidAmounts;
import com.rekindled.embers.util.Misc;
import com.rekindled.embers.util.sound.ISoundController;

import net.minecraft.ChatFormatting;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class MixerCentrifugeBottomBlockEntity extends BlockEntity implements IMechanicallyPowered, ISoundController, IExtraDialInformation, IExtraCapabilityInformation {

	public static final double EMBER_COST = 2.0;

	public MixerFluidTank north = new MixerFluidTank(FluidType.BUCKET_VOLUME * 8, this);
	public MixerFluidTank south = new MixerFluidTank(FluidType.BUCKET_VOLUME * 8, this);
	public MixerFluidTank east = new MixerFluidTank(FluidType.BUCKET_VOLUME * 8, this);
	public MixerFluidTank west = new MixerFluidTank(FluidType.BUCKET_VOLUME * 8, this);
	public MixerFluidTank[] tanks = new MixerFluidTank[]{north, south, east, west};

	private final LazyOptional<IFluidHandler> holderNorth = LazyOptional.of(() -> north);
	private final LazyOptional<IFluidHandler> holderSouth = LazyOptional.of(() -> south);
	private final LazyOptional<IFluidHandler> holderEast = LazyOptional.of(() -> east);
	private final LazyOptional<IFluidHandler> holderWest = LazyOptional.of(() -> west);

	public boolean loaded = false;
	boolean isWorking;

	public static final int SOUND_PROCESS = 1;
	public static final int[] SOUND_IDS = new int[]{SOUND_PROCESS};

	HashSet<Integer> soundsPlaying = new HashSet<>();
	private List<IUpgradeProvider> upgrades;
	private double powerRatio;
	public MixingRecipe cachedRecipe = null;

	public MixerCentrifugeBottomBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.MIXER_CENTRIFUGE_BOTTOM_ENTITY.get(), pPos, pBlockState);
	}

	public MixerFluidTank[] getTanks() {
		return tanks;
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		north.readFromNBT(nbt.getCompound("northTank"));
		south.readFromNBT(nbt.getCompound("southTank"));
		east.readFromNBT(nbt.getCompound("eastTank"));
		west.readFromNBT(nbt.getCompound("westTank"));
		isWorking = nbt.getBoolean("working");
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		nbt.put("northTank", north.writeToNBT(new CompoundTag()));
		nbt.put("southTank", south.writeToNBT(new CompoundTag()));
		nbt.put("eastTank", east.writeToNBT(new CompoundTag()));
		nbt.put("westTank", west.writeToNBT(new CompoundTag()));
		nbt.putBoolean("working", isWorking);
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

	public static void clientTick(Level level, BlockPos pos, BlockState state, MixerCentrifugeBottomBlockEntity blockEntity) {
		blockEntity.handleSound();
		//I know I'm supposed to use onLoad for stuff on the first tick but the tanks aren't synced to the client yet when that happens
		if (!blockEntity.loaded) {
			for (MixerFluidTank tank : blockEntity.tanks) {
				tank.previousFluid = tank.getFluidAmount();
			}
			blockEntity.loaded = true;
		}
		for (MixerFluidTank tank : blockEntity.tanks) {
			if (tank.getFluidAmount() != tank.previousFluid) {
				tank.renderOffset = tank.renderOffset + tank.getFluidAmount() - tank.previousFluid;
				tank.previousFluid = tank.getFluidAmount();
			}
		}
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, MixerCentrifugeBottomBlockEntity blockEntity) {
		MixerCentrifugeTopBlockEntity top = (MixerCentrifugeTopBlockEntity) level.getBlockEntity(pos.above());
		boolean wasWorking = blockEntity.isWorking;
		blockEntity.isWorking = false;
		if (top != null) {
			blockEntity.upgrades = UpgradeUtil.getUpgrades(level, pos.above(), Direction.values());
			UpgradeUtil.verifyUpgrades(blockEntity, blockEntity.upgrades);
			if (UpgradeUtil.doTick(blockEntity, blockEntity.upgrades))
				return;

			MixingContext context = new MixingContext(blockEntity.tanks);
			blockEntity.cachedRecipe = Misc.getRecipe(blockEntity.cachedRecipe, RegistryManager.MIXING.get(), context, level);
			/*if (recipe != null)
				blockEntity.powerRatio = recipe.getPowerRatio();
			else*/
			blockEntity.powerRatio = 0;
			double emberCost = UpgradeUtil.getTotalEmberConsumption(blockEntity, EMBER_COST, blockEntity.upgrades);
			if (top.capability.getEmber() >= emberCost && blockEntity.cachedRecipe != null) {
				boolean cancel = UpgradeUtil.doWork(blockEntity, blockEntity.upgrades);
				if(!cancel) {
					IFluidHandler tank = top.getCapability(ForgeCapabilities.FLUID_HANDLER).orElse(null);
					FluidStack output = blockEntity.cachedRecipe.getOutput(context);
					output = UpgradeUtil.transformOutput(blockEntity, output, blockEntity.upgrades);
					int amount = tank.fill(output, FluidAction.SIMULATE);
					if (amount != 0) {
						UpgradeUtil.throwEvent(blockEntity, new MachineRecipeEvent.Success<>(blockEntity, blockEntity.cachedRecipe), blockEntity.upgrades);
						blockEntity.isWorking = true;
						tank.fill(output, FluidAction.EXECUTE);
						blockEntity.cachedRecipe.process(context);
						UpgradeUtil.throwEvent(blockEntity, new EmberEvent(blockEntity, EmberEvent.EnumType.CONSUME, emberCost), blockEntity.upgrades);
						top.capability.removeAmount(emberCost, true);
					}
				}
			}
		}
		if (wasWorking != blockEntity.isWorking)
			blockEntity.setChanged();
	}

	@Override
	public void playSound(int id) {
		switch (id) {
		case SOUND_PROCESS:
			EmbersSounds.playMachineSound(this, SOUND_PROCESS, EmbersSounds.MIXER_LOOP.get(), SoundSource.BLOCKS, true, 1.0f, 1.0f, (float)worldPosition.getX()+0.5f,(float)worldPosition.getY()+1.0f,(float)worldPosition.getZ()+0.5f);
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

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == ForgeCapabilities.FLUID_HANDLER) {
			switch (side) {
			case DOWN:
				break;
			case EAST:
				return holderEast.cast();
			case NORTH:
				return holderNorth.cast();
			case SOUTH:
				return holderSouth.cast();
			case UP:
				break;
			case WEST:
				return holderWest.cast();
			}
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		holderNorth.invalidate();
		holderSouth.invalidate();
		holderEast.invalidate();
		holderWest.invalidate();
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
	public boolean hasCapabilityDescription(Capability<?> capability) {
		return capability == ForgeCapabilities.FLUID_HANDLER;
	}

	@Override
	public void addDialInformation(Direction facing, List<String> information, String dialType) {
		if (FluidDialBlock.DIAL_TYPE.equals(dialType)) {
			information.clear();
			information.add((facing == Direction.NORTH ? ChatFormatting.BOLD.toString() : "")+I18n.get(Embers.MODID + ".tooltip.side.north")+ChatFormatting.RESET.toString()+": "+FluidDialBlock.formatFluidStack(north.getFluid(),north.getCapacity()));
			if (north.getFluid().getFluid().is(EmbersFluidTags.INGOT_TOOLTIP) && north.getFluid().getAmount() >= FluidAmounts.NUGGET_AMOUNT)
				information.add(FluidAmounts.getIngotTooltip(north.getFluid().getAmount()));
			information.add((facing == Direction.EAST ? ChatFormatting.BOLD.toString() : "")+I18n.get(Embers.MODID + ".tooltip.side.east")+ChatFormatting.RESET.toString()+": "+FluidDialBlock.formatFluidStack(east.getFluid(),east.getCapacity()));
			if (east.getFluid().getFluid().is(EmbersFluidTags.INGOT_TOOLTIP) && east.getFluid().getAmount() >= FluidAmounts.NUGGET_AMOUNT)
				information.add(FluidAmounts.getIngotTooltip(east.getFluid().getAmount()));
			information.add((facing == Direction.SOUTH ? ChatFormatting.BOLD.toString() : "")+I18n.get(Embers.MODID + ".tooltip.side.south")+ChatFormatting.RESET.toString()+": "+FluidDialBlock.formatFluidStack(south.getFluid(),south.getCapacity()));
			if (south.getFluid().getFluid().is(EmbersFluidTags.INGOT_TOOLTIP) && south.getFluid().getAmount() >= FluidAmounts.NUGGET_AMOUNT)
				information.add(FluidAmounts.getIngotTooltip(south.getFluid().getAmount()));
			information.add((facing == Direction.WEST ? ChatFormatting.BOLD.toString() : "")+I18n.get(Embers.MODID + ".tooltip.side.west")+ChatFormatting.RESET.toString()+": "+FluidDialBlock.formatFluidStack(west.getFluid(),west.getCapacity()));
			if (west.getFluid().getFluid().is(EmbersFluidTags.INGOT_TOOLTIP) && west.getFluid().getAmount() >= FluidAmounts.NUGGET_AMOUNT)
				information.add(FluidAmounts.getIngotTooltip(west.getFluid().getAmount()));
		}
	}

	@Override
	public void addCapabilityDescription(List<String> strings, Capability<?> capability, Direction facing) {
		strings.add(IExtraCapabilityInformation.formatCapability(EnumIOType.INPUT, Embers.MODID + ".tooltip.goggles.fluid", I18n.get(Embers.MODID + ".tooltip.goggles.fluid.metal")));
	}

	@Override
	public double getMinimumPower() {
		return 20;
	}

	@Override
	public double getMechanicalSpeed(double power) {
		return Misc.getDiminishedPower(power,80,1.5/80);
	}

	@Override
	public double getNominalSpeed() {
		return 1;
	}

	@Override
	public double getStandardPowerRatio() {
		return powerRatio;
	}

	public static class MixerFluidTank extends FluidTank {

		public final BlockEntity entity;
		public float renderOffset;
		public int previousFluid;

		public MixerFluidTank(int capacity, BlockEntity entity) {
			super(capacity);
			this.entity = entity;
		}

		@Override
		public void onContentsChanged() {
			entity.setChanged();
		}
	}
}
