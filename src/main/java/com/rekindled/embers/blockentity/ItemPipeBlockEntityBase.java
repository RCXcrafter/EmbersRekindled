package com.rekindled.embers.blockentity;

import java.util.ArrayList;
import java.util.Random;

import com.mojang.math.Vector3f;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public abstract class ItemPipeBlockEntityBase extends BlockEntity implements IItemPipePriority {

	public static final int PRIORITY_BLOCK = 0;
	public static final int PRIORITY_PIPE = PRIORITY_BLOCK;

	public static Random random = new Random();
	boolean[] from = new boolean[Direction.values().length]; //just in case they like make minecraft 4 dimensional or something
	public boolean clogged = false;
	public ItemStackHandler inventory;
	public LazyOptional<IItemHandler> holder = LazyOptional.of(() -> inventory);
	Direction lastTransfer;
	boolean syncInventory = true;
	boolean syncCloggedFlag = true;
	boolean syncTransfer = true;
	int ticksExisted;
	int lastRobin;

	public ItemPipeBlockEntityBase(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
		super(pType, pPos, pBlockState);
		initInventory();
	}

	protected void initInventory() {
		inventory = new ItemStackHandler(1) {
			@Override
			public int getSlotLimit(int slot) {
				return ItemPipeBlockEntityBase.this.getCapacity();
			}

			@Override
			protected void onContentsChanged(int slot) {
				ItemPipeBlockEntityBase.this.syncInventory = true;
				ItemPipeBlockEntityBase.this.setChanged();
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
			if (tile instanceof ItemPipeBlockEntityBase && !((ItemPipeBlockEntityBase) tile).clogged)
				return true;
		}
		return false;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, ItemPipeBlockEntityBase blockEntity) {
		blockEntity.ticksExisted++;
		boolean itemsMoved = false;
		ItemStack passStack = blockEntity.inventory.extractItem(0, 1, true);
		if (!passStack.isEmpty()) {
			PipePriorityMap<Integer, Direction> possibleDirections = new PipePriorityMap<>();
			IItemHandler[] itemHandlers = new IItemHandler[Direction.values().length];

			for (Direction facing : Direction.values()) {
				if (!blockEntity.isConnected(facing))
					continue;
				if (blockEntity.isFrom(facing))
					continue;
				BlockEntity tile = level.getBlockEntity(pos.relative(facing));
				if (tile != null) {
					IItemHandler handler = tile.getCapability(ForgeCapabilities.ITEM_HANDLER, facing.getOpposite()).orElse(null);
					if (handler != null) {
						int priority = PRIORITY_BLOCK;
						if (tile instanceof IItemPipePriority)
							priority = ((IItemPipePriority) tile).getPriority(facing.getOpposite());
						if (blockEntity.isFrom(facing.getOpposite()))
							priority -= 5; //aka always try opposite first
						possibleDirections.put(priority, facing);
						itemHandlers[facing.get3DDataValue()] = handler;
					}
				}
			}

			for (int key : possibleDirections.keySet()) {
				ArrayList<Direction> list = possibleDirections.get(key);
				for(int i = 0; i < list.size(); i++) {
					Direction facing = list.get((i+blockEntity.lastRobin) % list.size());
					IItemHandler handler = itemHandlers[facing.get3DDataValue()];
					itemsMoved = blockEntity.pushStack(passStack, facing, handler);
					if(blockEntity.lastTransfer != facing) {
						blockEntity.syncTransfer = true;
						blockEntity.lastTransfer = facing;
						blockEntity.setChanged();
					}
					if(itemsMoved) {
						blockEntity.lastRobin++;
						break;
					}
				}
				if(itemsMoved)
					break;
			}
		}

		if (blockEntity.inventory.getStackInSlot(0).isEmpty()) {
			if(blockEntity.lastTransfer != null && !itemsMoved) {
				blockEntity.syncTransfer = true;
				blockEntity.lastTransfer = null;
				blockEntity.setChanged();
			}
			itemsMoved = true;
			blockEntity.resetFrom();
		}
		if (blockEntity.clogged == itemsMoved) {
			blockEntity.clogged = !itemsMoved;
			blockEntity.syncCloggedFlag = true;
			blockEntity.setChanged();
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static void clientTick(Level level, BlockPos pos, BlockState state, ItemPipeBlockEntityBase blockEntity) {
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

	private boolean pushStack(ItemStack passStack, Direction facing, IItemHandler handler) {
		int slot = -1;
		for (int j = 0; j < handler.getSlots() && slot == -1; j++) {
			if (handler.insertItem(j, passStack, true).isEmpty()) {
				slot = j;
			}
		}

		if (slot != -1) {
			ItemStack added = handler.insertItem(slot, passStack, false);
			if (added.isEmpty()) {
				this.inventory.extractItem(0, 1, false);
				return true;
			}
		}

		if (isFrom(facing))
			setFrom(facing, false);
		return false;
	}

	protected void resetSync() {
		syncInventory = false;
		syncCloggedFlag = false;
		syncTransfer = false;
	}

	protected boolean requiresSync() {
		return syncInventory || syncCloggedFlag || syncTransfer;
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		if (nbt.contains("clogged"))
			clogged = nbt.getBoolean("clogged");
		if (nbt.contains("inventory"))
			inventory.deserializeNBT(nbt.getCompound("inventory"));
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
		writeInventory(nbt);
		writeCloggedFlag(nbt);
		writeLastTransfer(nbt);
		for(Direction facing : Direction.values())
			nbt.putBoolean("from"+facing.get3DDataValue(),from[facing.get3DDataValue()]);
		nbt.putInt("lastRobin",lastRobin);
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag nbt = super.getUpdateTag();
		if (syncInventory)
			writeInventory(nbt);
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

	private void writeInventory(CompoundTag nbt) {
		nbt.put("inventory", inventory.serializeNBT());
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (!this.remove && cap == ForgeCapabilities.ITEM_HANDLER) {
			return ForgeCapabilities.ITEM_HANDLER.orEmpty(cap, holder);
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
