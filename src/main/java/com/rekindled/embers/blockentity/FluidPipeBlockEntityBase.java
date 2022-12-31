package com.rekindled.embers.blockentity;

import java.util.ArrayList;
import java.util.Random;

import com.mojang.math.Vector3f;
import com.rekindled.embers.api.tile.IFluidPipePriority;
import com.rekindled.embers.block.PipeBlockBase.PipeConnection;
import com.rekindled.embers.particle.GlowParticleOptions;
import com.rekindled.embers.util.Misc;
import com.rekindled.embers.util.PipePriorityMap;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public abstract class FluidPipeBlockEntityBase extends BlockEntity implements IFluidPipePriority {

	public static final int PRIORITY_BLOCK = 0;
	public static final int PRIORITY_PIPE = PRIORITY_BLOCK;
	public static final int MAX_PUSH = 120;

	static Random random = new Random();
	boolean[] from = new boolean[Direction.values().length]; //just in case they like make minecraft 4 dimensional or something
	public boolean clogged = false;
	public FluidTank tank;
	public LazyOptional<IFluidHandler> holder = LazyOptional.of(() -> tank);
	Direction lastTransfer;
	boolean syncTank = true;
	boolean syncCloggedFlag = true;
	boolean syncTransfer = true;
	int ticksExisted;
	int lastRobin;

	public FluidPipeBlockEntityBase(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
		super(pType, pPos, pBlockState);
		initFluidTank();
	}

	protected void initFluidTank() {
		tank = new FluidTank(getCapacity()) {
			@Override
			protected void onContentsChanged() {
				FluidPipeBlockEntityBase.this.syncTank = true;
				FluidPipeBlockEntityBase.this.setChanged();
			}
		};
	}

	public void onLoad() {
		syncTransfer = true;
		syncCloggedFlag = true;
		if (level instanceof ServerLevel serverLevel) {
			for (ServerPlayer serverplayer : serverLevel.getServer().getPlayerList().getPlayers()) {
				serverplayer.connection.send(this.getUpdatePacket());
			}
			this.resetSync();
		}
	}

	public abstract int getCapacity();

	@Override
	public int getPriority(Direction facing) {
		return PRIORITY_PIPE;
	}

	public abstract PipeConnection getInternalConnection(Direction facing);

	/**
	 * @param facing
	 * @return Whether items can be transferred through this side
	 */
	abstract boolean isConnected(Direction facing);

	public void setFrom(Direction facing, boolean flag) {
		from[facing.get3DDataValue()] = flag;
	}

	public void resetFrom() {
		for (Direction facing : Direction.values()) {
			setFrom(facing, false);
		}
	}

	protected boolean isFrom(Direction facing) {
		return from[facing.get3DDataValue()];
	}

	protected boolean isAnySideUnclogged() {
		for (Direction facing : Direction.values()) {
			if (!isConnected(facing))
				continue;
			BlockEntity tile = level.getBlockEntity(worldPosition.relative(facing));
			if (tile instanceof FluidPipeBlockEntityBase && !((FluidPipeBlockEntityBase) tile).clogged)
				return true;
		}
		return false;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, FluidPipeBlockEntityBase blockEntity) {
		blockEntity.ticksExisted++;
		boolean fluidMoved = false;
		FluidStack passStack = blockEntity.tank.drain(MAX_PUSH, FluidAction.SIMULATE);
		if (!passStack.isEmpty()) {
			PipePriorityMap<Integer, Direction> possibleDirections = new PipePriorityMap<>();
			IFluidHandler[] fluidHandlers = new IFluidHandler[Direction.values().length];

			for (Direction facing : Direction.values()) {
				if (!blockEntity.isConnected(facing))
					continue;
				if (blockEntity.isFrom(facing))
					continue;
				BlockEntity tile = level.getBlockEntity(pos.relative(facing));
				if (tile != null) {
					IFluidHandler handler = tile.getCapability(ForgeCapabilities.FLUID_HANDLER, facing.getOpposite()).orElse(null);
					if (handler != null) {
						int priority = PRIORITY_BLOCK;
						if (tile instanceof IFluidPipePriority)
							priority = ((IFluidPipePriority) tile).getPriority(facing.getOpposite());
						if (blockEntity.isFrom(facing.getOpposite()))
							priority -= 5; //aka always try opposite first
						possibleDirections.put(priority, facing);
						fluidHandlers[facing.get3DDataValue()] = handler;
					}
				}
			}

			for (int key : possibleDirections.keySet()) {
				ArrayList<Direction> list = possibleDirections.get(key);
				for (int i = 0; i < list.size(); i++) {
					Direction facing = list.get((i + blockEntity.lastRobin) % list.size());
					IFluidHandler handler = fluidHandlers[facing.get3DDataValue()];
					fluidMoved = blockEntity.pushStack(passStack, facing, handler);
					if (blockEntity.lastTransfer != facing) {
						blockEntity.syncTransfer = true;
						blockEntity.lastTransfer = facing;
						blockEntity.setChanged();
					}
					if (fluidMoved) {
						blockEntity.lastRobin++;
						break;
					}
				}
				if (fluidMoved)
					break;
			}
		}

		//if (fluidMoved)
		//    resetFrom();
		if (blockEntity.tank.getFluidAmount() <= 0) {
			if (blockEntity.lastTransfer != null && !fluidMoved) {
				blockEntity.syncTransfer = true;
				blockEntity.lastTransfer = null;
				blockEntity.setChanged();
			}
			fluidMoved = true;
			blockEntity.resetFrom();
		}
		if (blockEntity.clogged == fluidMoved) {
			blockEntity.clogged = !fluidMoved;
			blockEntity.syncCloggedFlag = true;
			blockEntity.setChanged();
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static void clientTick(Level level, BlockPos pos, BlockState state, FluidPipeBlockEntityBase blockEntity) {
		if (blockEntity.lastTransfer != null && Misc.isWearingLens(Minecraft.getInstance().player)) {
			float vx = blockEntity.lastTransfer.getStepX() / 1;
			float vy = blockEntity.lastTransfer.getStepY() / 1;
			float vz = blockEntity.lastTransfer.getStepZ() / 1;
			double x = pos.getX() + 0.4f + random.nextFloat() * 0.2f;
			double y = pos.getY() + 0.4f + random.nextFloat() * 0.2f;
			double z = pos.getZ() + 0.4f + random.nextFloat() * 0.2f;
			float r = blockEntity.clogged ? 255f : 16f;
			float g = blockEntity.clogged ? 16f : 255f;
			float b = 16f;
			for(int i = 0; i < 3; i++) {
				level.addParticle(new GlowParticleOptions(new Vector3f(r / 255.0F, g / 255.0F, b / 255.0F), new Vector3f(vx, vy, vz), 2.0f), x, y, z, vx, vy, vz);
			}
		}
	}

	private boolean pushStack(FluidStack passStack, Direction facing, IFluidHandler handler) {
		int added = handler.fill(passStack, FluidAction.SIMULATE);
		if (added > 0) {
			handler.fill(passStack, FluidAction.EXECUTE);
			this.tank.drain(added, FluidAction.EXECUTE);
			passStack.setAmount(passStack.getAmount() - added);
			return passStack.getAmount() <= 0;
		}

		if (isFrom(facing))
			setFrom(facing, false);
		return false;
	}

	protected void resetSync() {
		syncTank = false;
		syncCloggedFlag = false;
		syncTransfer = false;
	}

	protected boolean requiresSync() {
		return syncTank || syncCloggedFlag || syncTransfer;
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		if (nbt.contains("clogged"))
			clogged = nbt.getBoolean("clogged");
		if (nbt.contains("tank"))
			tank.readFromNBT(nbt.getCompound("tank"));
		if (nbt.contains("lastTransfer"))
			lastTransfer = Misc.readNullableFacing(nbt.getInt("lastTransfer"));
		for(Direction facing : Direction.values())
			if(nbt.contains("from"+facing.get3DDataValue()))
				from[facing.get3DDataValue()] = nbt.getBoolean("from"+facing.get3DDataValue());
		if (nbt.contains("lastRobin"))
			lastRobin = nbt.getInt("lastRobin");
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		writeTank(nbt);
		writeCloggedFlag(nbt);
		writeLastTransfer(nbt);
		for(Direction facing : Direction.values())
			nbt.putBoolean("from"+facing.get3DDataValue(),from[facing.get3DDataValue()]);
		nbt.putInt("lastRobin",lastRobin);
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag nbt = super.getUpdateTag();
		if (syncTank)
			writeTank(nbt);
		if (syncCloggedFlag)
			writeCloggedFlag(nbt);
		if (syncTransfer)
			writeLastTransfer(nbt);
		return nbt;
	}

	private void writeCloggedFlag(CompoundTag nbt) {
		nbt.putBoolean("clogged", clogged);
	}

	private void writeLastTransfer(CompoundTag nbt) {
		nbt.putInt("lastTransfer", Misc.writeNullableFacing(lastTransfer));
	}

	private void writeTank(CompoundTag nbt) {
		nbt.put("tank", tank.writeToNBT(new CompoundTag()));
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (!this.remove && cap == ForgeCapabilities.FLUID_HANDLER) {
			return holder.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (requiresSync() && level instanceof ServerLevel serverLevel) {
			for (ServerPlayer serverplayer : serverLevel.getServer().getPlayerList().getPlayers()) {
				serverplayer.connection.send(this.getUpdatePacket());
			}
			this.resetSync();
		}
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		holder.invalidate();
	}
}
