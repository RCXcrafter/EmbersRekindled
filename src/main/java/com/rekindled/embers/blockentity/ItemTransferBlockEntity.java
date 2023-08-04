package com.rekindled.embers.blockentity;

import java.util.Random;

import org.joml.Vector3f;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.filter.FilterItem;
import com.rekindled.embers.api.filter.IFilter;
import com.rekindled.embers.api.item.IFilterItem;
import com.rekindled.embers.block.PipeBlockBase.PipeConnection;
import com.rekindled.embers.particle.VaporParticleOptions;
import com.rekindled.embers.util.FilterUtil;
import com.rekindled.embers.util.Misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class ItemTransferBlockEntity extends ItemPipeBlockEntityBase {

	public static final int PRIORITY_TRANSFER = -10;
	public ItemStack filterItem = ItemStack.EMPTY;
	Random random = new Random();
	public boolean syncFilter = true;
	IItemHandler outputSide;
	public LazyOptional<IItemHandler> outputHolder = LazyOptional.of(() -> outputSide);

	IFilter filter = FilterUtil.FILTER_ANY;

	public ItemTransferBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.ITEM_TRANSFER_ENTITY.get(), pPos, pBlockState);
	}

	@Override
	protected void initInventory() {
		inventory = new ItemStackHandler(1) {
			@Override
			public int getSlotLimit(int slot) {
				return ItemTransferBlockEntity.this.getCapacity();
			}

			@Override
			protected void onContentsChanged(int slot) {
				ItemTransferBlockEntity.this.syncInventory = true;
				ItemTransferBlockEntity.this.setChanged();
			}

			@Override
			public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
				if (ItemTransferBlockEntity.this.acceptsItem(stack))
					return super.insertItem(slot, stack, simulate);
				else
					return stack;
			}

			@Override
			public boolean isItemValid(int slot, ItemStack stack) {
				return ItemTransferBlockEntity.this.acceptsItem(stack);
			}
		};
		outputSide = Misc.makeRestrictedItemHandler(inventory, false, true);
	}

	@Override
	public void onLoad() {
		syncFilter = true;
		super.onLoad();
	}

	public boolean acceptsItem(ItemStack stack) {
		return filter.acceptsItem(stack);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		if (nbt.contains("filter")) {
			filterItem = ItemStack.of(nbt.getCompound("filter"));
		}
		setupFilter();
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
		nbt.put("filter", filterItem.serializeNBT());
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

	public void setupFilter() {
		Item item = this.filterItem.getItem();
		if(item instanceof IFilterItem)
			filter = ((IFilterItem) item).getFilter(this.filterItem);
		else if(!this.filterItem.isEmpty())
			filter = new FilterItem(this.filterItem);
		else
			filter = FilterUtil.FILTER_ANY;
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, ItemTransferBlockEntity blockEntity) {
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
		ItemPipeBlockEntityBase.serverTick(level, pos, state, blockEntity);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (!this.remove && cap == ForgeCapabilities.ITEM_HANDLER) {
			if (side == null)
				return ForgeCapabilities.ITEM_HANDLER.orEmpty(cap, holder);
			if (level.getBlockState(this.getBlockPos()).hasProperty(BlockStateProperties.FACING)) {
				Direction facing = level.getBlockState(this.getBlockPos()).getValue(BlockStateProperties.FACING);
				if (side.getOpposite() == facing)
					return ForgeCapabilities.ITEM_HANDLER.orEmpty(cap, outputHolder);
				else if (side.getAxis() == facing.getAxis())
					return ForgeCapabilities.ITEM_HANDLER.orEmpty(cap, holder);
			}
		}
		return LazyOptional.empty();
	}

	@Override
	public int getCapacity() {
		return 4;
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
