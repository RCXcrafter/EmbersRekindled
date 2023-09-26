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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidPipeBlockEntity extends FluidPipeBlockEntityBase {

	IFluidHandler[] sideHandlers;

	public FluidPipeBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.FLUID_PIPE_ENTITY.get(), pPos, pBlockState);
	}

	@Override
	protected void initFluidTank() {
		super.initFluidTank();
		sideHandlers = new IFluidHandler[Direction.values().length];
		for (Direction facing : Direction.values()) {
			sideHandlers[facing.get3DDataValue()] = new IFluidHandler() {

				@Override
				public int fill(FluidStack resource, FluidAction action) {
					if(action.execute())
						setFrom(facing, true);
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

	public static void serverTick(Level level, BlockPos pos, BlockState state, FluidPipeBlockEntity blockEntity) {
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
