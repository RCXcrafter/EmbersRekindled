package com.rekindled.embers.blockentity;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.rekindled.embers.RegistryManager;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class ItemVacuumBlockEntity extends BlockEntity {

	public ItemStackHandler inventory = new ItemStackHandler(1) {
		@Override
		public boolean isItemValid(int slot, @NotNull ItemStack stack) {
			return false;
		}
	};
	public LazyOptional<IItemHandler> holder = LazyOptional.of(() -> inventory);

	public ItemVacuumBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.ITEM_VACUUM_ENTITY.get(), pPos, pBlockState);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, ItemVacuumBlockEntity blockEntity) {
		Direction facing = state.getValue(BlockStateProperties.FACING);
		BlockEntity tile = level.getBlockEntity(pos.relative(facing.getOpposite()));
		if (level.hasNeighborSignal(pos) && tile != null) {
			IItemHandler inventory = tile.getCapability(ForgeCapabilities.ITEM_HANDLER, facing).orElse(null);
			if (inventory != null) {
				Vec3i vec = facing.getNormal();
				AABB suckBB = new AABB(pos.getX() - 6 + vec.getX() * 7, pos.getY() - 6 + vec.getY() * 7, pos.getZ() - 6 + vec.getZ() * 7, pos.getX() + 7 + vec.getX() * 7, pos.getY() + 7 + vec.getY() * 7, pos.getZ() + 7 + vec.getZ() * 7);
				List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, suckBB, entity -> getInsertedSlot(entity.getItem(), inventory) != -1);
				if (items.size() > 0) {
					for (ItemEntity item : items) {
						Vec3 v = new Vec3(item.getX() - (pos.getX() + 0.5), item.getY() - (pos.getY() + 0.5), item.getZ() - (pos.getZ() + 0.5));
						double factor = 1.0 / v.length();
						double speed = factor * 0.4 + 0.06;
						factor += 1.0;
						v = v.normalize().multiply(speed, speed, speed);
						item.setDeltaMovement(-v.x + item.getDeltaMovement().x / factor, -v.y + item.getDeltaMovement().y / factor, -v.z + item.getDeltaMovement().z / factor);
					}
				}
				if (!level.isClientSide) {
					List<ItemEntity> nearestItems = level.getEntitiesOfClass(ItemEntity.class, new AABB(pos.getX() + 0.24 + vec.getX() * 0.25, pos.getY() + 0.24 + vec.getY() * 0.25, pos.getZ() + 0.24 + vec.getZ() * 0.25, pos.getX() + 0.76 + vec.getX() * 0.25, pos.getY() + 0.76 + vec.getY() * 0.25, pos.getZ() + 0.76 + vec.getZ() * 0.25));
					if (nearestItems.size() > 0) {
						for (ItemEntity item : nearestItems) {
							if (item.isRemoved())
								continue;
							int slot = getInsertedSlot(item.getItem(), inventory);
							if (slot != -1) {
								item.setItem(inventory.insertItem(slot, item.getItem(), false));
								if (item.getItem().isEmpty()) {
									item.discard();
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		Direction facing = level.getBlockState(worldPosition).getValue(BlockStateProperties.FACING);
		if (!this.remove && cap == ForgeCapabilities.ITEM_HANDLER && side == facing.getOpposite()) {
			return ForgeCapabilities.ITEM_HANDLER.orEmpty(cap, holder);
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		holder.invalidate();
	}

	static int getInsertedSlot(ItemStack stack, IItemHandler inventory) {
		int slot = -1;
		for (int j = 0; j < inventory.getSlots() && slot == -1; j++) {
			if (inventory.isItemValid(j, stack)) {
				ItemStack added = inventory.insertItem(j, stack, true);
				if (added.getCount() < stack.getCount() || !ItemStack.isSameItemSameTags(stack, added)) {
					slot = j;
				}
			}
		}
		return slot;
	}
}
