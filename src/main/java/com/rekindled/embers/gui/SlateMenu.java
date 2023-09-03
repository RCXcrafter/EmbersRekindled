package com.rekindled.embers.gui;

import java.util.ArrayList;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.item.AlchemyHintItem;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlateMenu extends AbstractContainerMenu {

	public static int slateHeight = 256;
	public static int slateWidth = 192;
	public static int slateMargin = 14;
	public static int invHeight = 100;
	public static int invWidth = 176;
	public static int slotHeight = 48;
	public static int slotCount = 7;
	public static int layerHeight = (slateHeight - 26 - 2 * slateMargin) / slotCount;
	public static int layerWidth = (slateWidth - 24 - 2 * slateMargin);

	public ItemStackHandler inventory;
	public ItemStack slate;

	public SlateMenu(int id, Inventory inv, ItemStack slate) {
		super(RegistryManager.SLATE_MENU.get(), id);
		this.slate = slate;
		inventory = new ItemStackHandler(slotCount) {
			public void onContentsChanged(int slot) {
				SlateMenu.this.slate.getOrCreateTag().put("inventory", this.serializeNBT());
			}
		};
		CompoundTag nbt = slate.getOrCreateTagElement("inventory");
		if (!nbt.isEmpty())
			inventory.deserializeNBT(nbt);

		//slate inventory
		Slot master = this.addSlot(new SlotItemHandler(inventory, 0, -33, slateMargin + layerHeight / 2 + 18));
		for (int j = 1; j < slotCount; ++j) {
			this.addSlot(new SlateSlot(inventory, j, -33, slateMargin + j * layerHeight + layerHeight / 2 + 18, master));
		}
		//player inventory
		for (int l = 0; l < 3; ++l) {
			for (int k = 0; k < 9; ++k) {
				this.addSlot(new Slot(inv, k + l * 9 + 9, 8 + k * 18, l * 18 + 274));
			}
		}
		//hotbar
		for (int i = 0; i < 9; ++i) {
			if (ItemStack.isSameItemSameTags(inv.getItem(i), slate)) {
				this.addSlot(new LockedSlot(inv, i, 8 + i * 18, 332));
			} else {
				this.addSlot(new Slot(inv, i, 8 + i * 18, 332));
			}
		}
	}

	public boolean stillValid(Player player) {
		return player.getMainHandItem() == slate || player.getOffhandItem() == slate;
	}

	@Override
	public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
		super.clicked(slotId, dragType, clickTypeIn, player);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (index < inventory.getSlots()) {
				if (!this.moveItemStackTo(itemstack1, inventory.getSlots(), this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(itemstack1, 0, inventory.getSlots(), false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.setByPlayer(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}

		return itemstack;
	}

	public void removed(Player p_39649_) {
		super.removed(p_39649_);
	}

	public static SlateMenu fromBuffer(int id, Inventory inv, FriendlyByteBuf buf) {
		return new SlateMenu(id, inv, buf.readItem());
	}

	public static class SlateSlot extends SlotItemHandler {

		public Slot master;

		public SlateSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, Slot master) {
			super(itemHandler, index, xPosition, yPosition);
			this.master = master;
		}

		public boolean mayPlace(ItemStack stack) {
			ItemStack masterItem = master.getItem();
			if (masterItem.isEmpty())
				return false;
			ArrayList<ItemStack> masterInputs = AlchemyHintItem.getInputs(masterItem);
			ArrayList<ItemStack> inputs = AlchemyHintItem.getInputs(stack);
			if (masterInputs.size() != inputs.size())
				return false;
			for (int i = 0; i < masterInputs.size(); ++i) {
				if (!ItemStack.isSameItem(masterInputs.get(i), inputs.get(i)))
					return false;
			}
			return true;
		}
	}

	public static class LockedSlot extends Slot {

		public LockedSlot(Container container, int id, int x, int y) {
			super(container, id, x, y);
		}

		public boolean mayPlace(ItemStack stack) {
			return false;
		}

		public boolean mayPickup(Player player) {
			return false;
		}

		public boolean allowModification(Player player) {
			return false;
		}
	}
}
