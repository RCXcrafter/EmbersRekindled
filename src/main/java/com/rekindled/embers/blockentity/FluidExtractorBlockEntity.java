package com.rekindled.embers.blockentity;

import java.util.Random;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.particle.VaporParticleOptions;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class FluidExtractorBlockEntity extends FluidPipeBlockEntityBase {

	Random random = new Random();
	IFluidHandler[] sideHandlers;
	boolean active;
	public static final int MAX_DRAIN = 120;

	public FluidExtractorBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.FLUID_EXTRACTOR_ENTITY.get(), pPos, pBlockState);
	}

	@Override
	protected void initFluidTank() {
		super.initFluidTank();
		sideHandlers = new IFluidHandler[Direction.values().length];
		for (Direction facing : Direction.values()) {
			sideHandlers[facing.get3DDataValue()] = new IFluidHandler() {

				@Override
				public int fill(FluidStack resource, FluidAction action) {
					if (active)
						return 0;
					if (action.execute())
						setFrom(facing,true);
					return tank.fill(resource, action);
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, FluidAction action) {
					return tank.drain(resource, action);
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, FluidAction action) {
					return tank.drain(maxDrain, action);
				}

				@Override
				public int getTanks() {
					return tank.getTanks();
				}

				@Override
				public @NotNull FluidStack getFluidInTank(int tankNum) {
					return tank.getFluidInTank(tankNum);
				}

				@Override
				public int getTankCapacity(int tankNum) {
					return tank.getTankCapacity(tankNum);
				}

				@Override
				public boolean isFluidValid(int tankNum, @NotNull FluidStack stack) {
					return tank.isFluidValid(tankNum, stack);
				}
			};
		}
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, FluidExtractorBlockEntity blockEntity) {
		if (level instanceof ServerLevel && blockEntity.clogged && blockEntity.isAnySideUnclogged()) {
			Random posRand = new Random(pos.asLong());
			double angleA = posRand.nextDouble() * Math.PI * 2;
			double angleB = posRand.nextDouble() * Math.PI * 2;
			float xOffset = (float) (Math.cos(angleA) * Math.cos(angleB));
			float yOffset = (float) (Math.sin(angleA) * Math.cos(angleB));
			float zOffset = (float) Math.sin(angleB);
			float speed = 0.1f;
			float vx = xOffset * speed + posRand.nextFloat() * speed * 0.3f;
			float vy = yOffset * speed + posRand.nextFloat() * speed * 0.3f;
			float vz = zOffset * speed + posRand.nextFloat() * speed * 0.3f;
			((ServerLevel) level).sendParticles(new VaporParticleOptions(new Vector3f(64.0f / 255.0F, 64.0f / 255.0F, 64.0f / 255.0F), new Vec3(vx, vy, vz), 1.0f), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 4, 0, 0, 0, 1.0);
		}
		blockEntity.active = level.hasNeighborSignal(pos);
		for (Direction facing : Direction.values()) {
			if (!blockEntity.getConnection(facing).transfer)
				continue;
			BlockEntity tile = level.getBlockEntity(pos.relative(facing));
			if (tile != null && !(tile instanceof FluidPipeBlockEntityBase)) {
				if (blockEntity.active) {
					IFluidHandler handler = tile.getCapability(ForgeCapabilities.FLUID_HANDLER, facing.getOpposite()).orElse(null);
					if (handler != null && handler.drain(MAX_DRAIN, FluidAction.SIMULATE) != null) {
						FluidStack extracted = handler.drain(MAX_DRAIN, FluidAction.SIMULATE);
						int filled = blockEntity.tank.fill(extracted, FluidAction.SIMULATE);
						if (filled > 0) {
							blockEntity.tank.fill(extracted, FluidAction.EXECUTE);
							handler.drain(filled, FluidAction.EXECUTE);
						}
					}
					blockEntity.setFrom(facing, true);
				} else {
					blockEntity.setFrom(facing, false);
				}
			}
		}
		FluidPipeBlockEntityBase.serverTick(level, pos, state, blockEntity);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (!this.remove && cap == ForgeCapabilities.FLUID_HANDLER) {
			if (side == null)
				return ForgeCapabilities.FLUID_HANDLER.orEmpty(cap, holder);
			else if (getConnection(side).transfer)
				return ForgeCapabilities.FLUID_HANDLER.orEmpty(cap, LazyOptional.of(() -> this.sideHandlers[side.get3DDataValue()]));
		}
		return super.getCapability(cap, side);
	}

	@Override
	public int getCapacity() {
		return 240;
	}
}
