package com.rekindled.embers.blockentity;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.block.PipeBlockBase;
import com.rekindled.embers.block.PipeBlockBase.PipeConnection;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

public class ItemPipeBlockEntity extends ItemPipeBlockEntityBase {

	IItemHandler[] sideHandlers;

	public ItemPipeBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.ITEM_PIPE_ENTITY.get(), pPos, pBlockState);
	}

	@Override
	protected void initInventory() {
		super.initInventory();
		sideHandlers = new IItemHandler[Direction.values().length];
		for (Direction facing : Direction.values()) {
			sideHandlers[facing.get3DDataValue()] = new IItemHandler() {
				@Override
				public int getSlots() {
					return inventory.getSlots();
				}

				@Nonnull
				@Override
				public ItemStack getStackInSlot(int slot) {
					return inventory.getStackInSlot(slot);
				}

				@Nonnull
				@Override
				public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
					if (!simulate)
						setFrom(facing, true);
					return inventory.insertItem(slot, stack, simulate);
				}

				@Nonnull
				@Override
				public ItemStack extractItem(int slot, int amount, boolean simulate) {
					return inventory.extractItem(slot, amount, simulate);
				}

				@Override
				public int getSlotLimit(int slot) {
					return inventory.getSlotLimit(slot);
				}

				@Override
				public boolean isItemValid(int slot, @NotNull ItemStack stack) {
					return true;
				}
			};
		}
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, ItemPipeBlockEntity blockEntity) {
		//if (level.isClientSide && blockEntity.clogged && blockEntity.isAnySideUnclogged())
		//Misc.spawnClogParticles(level, pos, 1, 0.25f);
		ItemPipeBlockEntityBase.serverTick(level, pos, state, blockEntity);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (!this.remove && cap == ForgeCapabilities.ITEM_HANDLER) {
			if (side == null)
				return ForgeCapabilities.ITEM_HANDLER.orEmpty(cap, holder);
			else if (getInternalConnection(side) != PipeConnection.NONE)
				return ForgeCapabilities.ITEM_HANDLER.orEmpty(cap, LazyOptional.of(() -> this.sideHandlers[side.get3DDataValue()]));
		}
		return super.getCapability(cap, side);
	}

	@Override
	int getCapacity() {
		return 4;
	}

	@Override
	public PipeConnection getInternalConnection(Direction facing) {
		return level.getBlockState(worldPosition).getValue(PipeBlockBase.DIRECTIONS[facing.get3DDataValue()]);
	}

	@Override
	boolean isConnected(Direction facing) {
		return getInternalConnection(facing) != PipeConnection.NONE;
	}
}
