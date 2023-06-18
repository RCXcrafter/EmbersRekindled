package com.rekindled.embers.blockentity;

import java.util.Random;

import com.mojang.math.Vector3f;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.block.PipeBlockBase.PipeConnection;
import com.rekindled.embers.particle.VaporParticleOptions;
import com.rekindled.embers.util.Misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class FluidTransferBlockEntity extends FluidPipeBlockEntityBase {

	public static final int PRIORITY_TRANSFER = -10;
	public FluidStack filterFluid = FluidStack.EMPTY;
	Random random = new Random();
	public boolean syncFilter;
	IFluidHandler outputSide;
	public LazyOptional<IFluidHandler> outputHolder = LazyOptional.of(() -> outputSide);

	public FluidTransferBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.FLUID_TRANSFER_ENTITY.get(), pPos, pBlockState);
	}

	@Override
	protected void initFluidTank() {
		tank = new FluidTank(getCapacity()) {
			@Override
			protected void onContentsChanged() {
				FluidTransferBlockEntity.this.setChanged();
			}

			@Override
			public int fill(FluidStack resource, FluidAction action) {
				if(!filterFluid.isEmpty()) {
					if(resource != null) {
						if (filterFluid.getTag() != null ? resource.isFluidEqual(filterFluid) : resource.getFluid() == filterFluid.getFluid()) {
							return super.fill(resource, action);
						}
					}
					return 0;
				}
				return super.fill(resource, action);
			}
		};
		outputSide = Misc.makeRestrictedFluidHandler(tank, false, true);
	}

	@Override
	public void onLoad() {
		syncFilter = true;
		super.onLoad();
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		if (nbt.contains("filter")) {
			filterFluid = FluidStack.loadFluidStackFromNBT(nbt.getCompound("filter"));
		}
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		writeFilter(nbt);
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag nbt = super.getUpdateTag();
		writeFilter(nbt);
		return nbt;
	}

	private void writeFilter(CompoundTag nbt) {
		nbt.put("filter", filterFluid.writeToNBT(new CompoundTag()));
	}

	@Override
	protected boolean requiresSync() {
		return syncFilter || super.requiresSync();
	}

	@Override
	protected void resetSync() {
		super.resetSync();
		syncFilter = false;
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, FluidTransferBlockEntity blockEntity) {
		if (level instanceof ServerLevel && blockEntity.clogged && blockEntity.isAnySideUnclogged()) {
			Random posRand = new Random(pos.asLong());
			double angleA = posRand.nextDouble() * Math.PI * 2;
			double angleB = posRand.nextDouble() * Math.PI * 2;
			float xOffset = (float) (Math.cos(angleA) * Math.cos(angleB));
			float yOffset = (float) (Math.sin(angleA) * Math.cos(angleB));
			float zOffset = (float) Math.sin(angleB);
			float speed = 0.1875f;
			float vx = xOffset * speed + posRand.nextFloat() * speed * 0.3f;
			float vy = yOffset * speed + posRand.nextFloat() * speed * 0.3f;
			float vz = zOffset * speed + posRand.nextFloat() * speed * 0.3f;
			((ServerLevel) level).sendParticles(new VaporParticleOptions(new Vector3f(64.0f / 255.0F, 64.0f / 255.0F, 64.0f / 255.0F), new Vec3(vx, vy, vz), 1.0f), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 4, 0, 0, 0, 1.0);
		}
		FluidPipeBlockEntityBase.serverTick(level, pos, state, blockEntity);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (!this.remove && cap == ForgeCapabilities.FLUID_HANDLER && level.getBlockState(this.getBlockPos()).hasProperty(BlockStateProperties.FACING)) {
			Direction facing = level.getBlockState(this.getBlockPos()).getValue(BlockStateProperties.FACING);
			if (side.getOpposite() == facing)
				return ForgeCapabilities.FLUID_HANDLER.orEmpty(cap, outputHolder);
			else if (side.getAxis() == facing.getAxis())
				return ForgeCapabilities.FLUID_HANDLER.orEmpty(cap, holder);
		}
		return LazyOptional.empty();
	}

	@Override
	public int getCapacity() {
		return 240;
	}

	@Override
	public int getPriority(Direction facing) {
		return PRIORITY_TRANSFER;
	}

	@Override
	public PipeConnection getInternalConnection(Direction facing) {
		return PipeConnection.NONE;
	}

	@Override
	boolean isConnected(Direction facing) {
		return level.getBlockState(this.getBlockPos()).getValue(BlockStateProperties.FACING).getAxis() == facing.getAxis();
	}

	@Override
	protected boolean isFrom(Direction facing) {
		return level.getBlockState(this.getBlockPos()).getValue(BlockStateProperties.FACING) == facing;
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		outputHolder.invalidate();
	}
}
