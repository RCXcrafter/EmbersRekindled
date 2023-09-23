package com.rekindled.embers.blockentity;

import java.util.List;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.event.DialInformationEvent;
import com.rekindled.embers.api.event.EmberEvent;
import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.api.tile.IExtraDialInformation;
import com.rekindled.embers.api.tile.IMechanicallyPowered;
import com.rekindled.embers.api.upgrades.UpgradeContext;
import com.rekindled.embers.api.upgrades.UpgradeUtil;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.power.DefaultEmberCapability;
import com.rekindled.embers.util.Misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class MechanicalPumpBottomBlockEntity extends BlockEntity implements IMechanicallyPowered, IExtraDialInformation {

	public IEmberCapability capability = new DefaultEmberCapability() {
		@Override
		public void onContentsChanged() {
			super.onContentsChanged();
			MechanicalPumpBottomBlockEntity.this.setChanged();
		}
	};
	public static final double EMBER_COST = 0.5;

	public int progress;
	public int totalProgress;
	public int lastProgress;
	private List<UpgradeContext> upgrades;

	public MechanicalPumpBottomBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.MECHANICAL_PUMP_BOTTOM_ENTITY.get(), pPos, pBlockState);
		capability.setEmberCapacity(1000);
	}

	@Override
	public AABB getRenderBoundingBox() {
		return new AABB(worldPosition.offset(0, 1, 0), worldPosition.offset(1, 3, 1));
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		capability.deserializeNBT(nbt);
		progress = nbt.getInt("progress");
		totalProgress = nbt.getInt("totalProgress");
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		capability.writeToNBT(nbt);
		nbt.putInt("progress", progress);
		nbt.putInt("totalProgress", totalProgress);
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
		if (!this.remove && cap == EmbersCapabilities.EMBER_CAPABILITY) {
			return capability.getCapability(cap, side);
		}
		return super.getCapability(cap, side);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, MechanicalPumpBottomBlockEntity blockEntity) {
		blockEntity.upgrades = UpgradeUtil.getUpgrades(level, pos, Misc.horizontals);
		UpgradeUtil.verifyUpgrades(blockEntity, blockEntity.upgrades);
		if (UpgradeUtil.doTick(blockEntity, blockEntity.upgrades))
			return;
		double emberCost = UpgradeUtil.getTotalEmberConsumption(blockEntity, EMBER_COST, blockEntity.upgrades);
		if (blockEntity.capability.getEmber() >= emberCost) {
			boolean cancel = UpgradeUtil.doWork(blockEntity, blockEntity.upgrades);
			if (!cancel) {
				int speed = (int) UpgradeUtil.getTotalSpeedModifier(blockEntity, blockEntity.upgrades);
				blockEntity.progress += speed;
				blockEntity.totalProgress += speed;
				UpgradeUtil.throwEvent(blockEntity, new EmberEvent(blockEntity, EmberEvent.EnumType.CONSUME, emberCost), blockEntity.upgrades);
				blockEntity.capability.removeAmount(emberCost, true);
				if (blockEntity.progress > 400) {
					blockEntity.progress -= 400;
					/*boolean doContinue = true;
					for (int r = 0; r < 6 && doContinue; r++) {
						for (int i = -r; i < r + 1 && doContinue; i++) {
							for (int j = -r; j < 1 && doContinue; j++) {
								for (int k = -r; k < r + 1 && doContinue; k++) {
									doContinue = blockEntity.attemptPump(pos.offset(i, j - 1, k));
								}
							}
						}
					}*/
					blockEntity.attemptPump(pos.below());
					blockEntity.playSound(speed);
				}
				blockEntity.setChanged();
			}
		}
	}

	public static void clientTick(Level level, BlockPos pos, BlockState state, MechanicalPumpBottomBlockEntity blockEntity) {
		blockEntity.upgrades = UpgradeUtil.getUpgrades(level, pos, Misc.horizontals);
		UpgradeUtil.verifyUpgrades(blockEntity, blockEntity.upgrades);
		blockEntity.lastProgress = blockEntity.totalProgress;
	}

	public boolean attemptPump(BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		if (state.getBlock() instanceof BucketPickup && !state.getFluidState().isEmpty() && state.getFluidState().isSource()) {
			FluidStack stack = new FluidStack(state.getFluidState().holder().get(), FluidType.BUCKET_VOLUME);
			if (stack != null) {
				MechanicalPumpTopBlockEntity t = (MechanicalPumpTopBlockEntity)level.getBlockEntity(worldPosition.above());
				int filled = t.getTank().fill(stack, FluidAction.SIMULATE);
				if (filled == stack.getAmount()) {
					t.getTank().fill(stack, FluidAction.EXECUTE);
					((BucketPickup) state.getBlock()).pickupBlock(level, pos, state);
					return false;
				}
			}
		}
		return true;
	}

	public void playSound(int speed) {
		float pitch;
		SoundEvent sound;
		if (speed >= 20) {
			sound = EmbersSounds.PUMP_FAST.get();
			pitch = speed / 20f;
		} else if(speed >= 10) {
			sound = EmbersSounds.PUMP_MID.get();
			pitch = speed / 10f;
		} else {
			sound = EmbersSounds.PUMP_SLOW.get();
			pitch = speed;
		}
		level.playSound(null, worldPosition.above(), sound, SoundSource.BLOCKS, 1.0f, pitch);
	}

	@Override
	public double getMechanicalSpeed(double power) {
		return Math.min(power/2, 100);
	}

	@Override
	public double getMinimumPower() {
		return 2;
	}

	@Override
	public double getNominalSpeed() {
		return 10;
	}

	@Override
	public void addDialInformation(Direction facing, List<String> information, String dialType) {
		UpgradeUtil.throwEvent(this, new DialInformationEvent(this, information, dialType), upgrades);
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
}
