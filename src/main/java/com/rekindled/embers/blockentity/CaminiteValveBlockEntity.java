package com.rekindled.embers.blockentity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.block.MechEdgeBlockBase;
import com.rekindled.embers.datagen.EmbersBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class CaminiteValveBlockEntity extends BlockEntity {

	int ticksExisted = 0;
	ReservoirBlockEntity reservoir;
	IFluidHandler fluidHandler;
	private final LazyOptional<IFluidHandler> holder = LazyOptional.of(() -> fluidHandler);

	public CaminiteValveBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.CAMINITE_VALVE_ENTITY.get(), pPos, pBlockState);
		fluidHandler = new IFluidHandler() {

			@Override
			public int getTanks() {
				if (reservoir != null)
					return reservoir.getCapability(ForgeCapabilities.FLUID_HANDLER, null).orElse(null).getTanks();
				return 0;
			}

			@Override
			public @NotNull FluidStack getFluidInTank(int tank) {
				if (reservoir != null)
					return reservoir.getCapability(ForgeCapabilities.FLUID_HANDLER, null).orElse(null).getFluidInTank(tank);
				return FluidStack.EMPTY;
			}

			@Override
			public int getTankCapacity(int tank) {
				if (reservoir != null)
					return reservoir.getCapability(ForgeCapabilities.FLUID_HANDLER, null).orElse(null).getTankCapacity(tank);
				return 0;
			}

			@Override
			public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
				if (reservoir != null)
					return reservoir.getCapability(ForgeCapabilities.FLUID_HANDLER, null).orElse(null).isFluidValid(tank, stack);
				return false;
			}

			@Override
			public int fill(FluidStack resource, FluidAction action) {
				if (reservoir != null)
					return reservoir.getCapability(ForgeCapabilities.FLUID_HANDLER, null).orElse(null).fill(resource, action);
				return 0;
			}

			@Override
			public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
				if (reservoir != null)
					return reservoir.getCapability(ForgeCapabilities.FLUID_HANDLER, null).orElse(null).drain(resource, action);
				return FluidStack.EMPTY;
			}

			@Override
			public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
				if (reservoir != null)
					return reservoir.getCapability(ForgeCapabilities.FLUID_HANDLER, null).orElse(null).drain(maxDrain, action);
				return FluidStack.EMPTY;
			}
		};
	}

	public ReservoirBlockEntity getReservoir() {
		return reservoir;
	}

	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction facing) {
		if (!this.remove && capability == ForgeCapabilities.FLUID_HANDLER && (facing == null || facing.getAxis() != Direction.Axis.Y))
			return holder.cast();
		return super.getCapability(capability, facing);
	}

	public void updateTank() {
		if (isRemoved() || !level.getBlockState(worldPosition).hasProperty(MechEdgeBlockBase.EDGE))
			return;
		reservoir = null;
		BlockPos basePos = worldPosition.offset(level.getBlockState(worldPosition).getValue(MechEdgeBlockBase.EDGE).centerPos);
		for (int i = 1; i < 64; i++) {
			BlockPos pos = basePos.below(i);
			if (!level.getBlockState(pos).is(EmbersBlockTags.RESERVOIR_EXPANSION)) {
				BlockEntity tile = level.getBlockEntity(pos);
				if (tile instanceof ReservoirBlockEntity) {
					reservoir = (ReservoirBlockEntity) tile;
				}
				break;
			}
		}
	}

	public static void commonTick(Level level, BlockPos pos, BlockState state, CaminiteValveBlockEntity blockEntity) {
		blockEntity.ticksExisted++;
		if (blockEntity.reservoir != null && blockEntity.reservoir.isRemoved())
			blockEntity.reservoir = null;
		if (blockEntity.ticksExisted % 20 == 0)
			blockEntity.updateTank();
	}
}
