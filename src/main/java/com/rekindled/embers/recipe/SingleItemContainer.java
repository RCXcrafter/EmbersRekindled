package com.rekindled.embers.recipe;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class SingleItemContainer implements Container {

	public ItemStack stack;

	public SingleItemContainer(ItemStack stack) {
		this.stack = stack;
	}

	@Override
	@Deprecated
	public void clearContent() { }

	@Override
	public int getContainerSize() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public ItemStack getItem(int pSlot) {
		return stack;
	}

	@Override
	@Deprecated
	public ItemStack removeItem(int pSlot, int pAmount) {
		return ItemStack.EMPTY;
	}

	@Override
	@Deprecated
	public ItemStack removeItemNoUpdate(int pSlot) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		this.stack = stack;
	}

	@Override
	@Deprecated
	public void setChanged() { }

	@Override
	@Deprecated
	public boolean stillValid(Player pPlayer) {
		return false;
	}
}
