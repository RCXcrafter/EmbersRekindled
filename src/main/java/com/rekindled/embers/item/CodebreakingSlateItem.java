package com.rekindled.embers.item;

import com.rekindled.embers.gui.SlateMenu;
import com.rekindled.embers.util.Misc;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;

public class CodebreakingSlateItem extends Item implements MenuProvider {

	public CodebreakingSlateItem(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		if (!level.isClientSide) {
			ItemStack slate = player.getItemInHand(hand);
			NetworkHooks.openScreen((ServerPlayer) player, this, buf -> {
				buf.writeItem(slate);
			});

			//give players their waste back if they have a legacy slate
			ItemStackHandler inventory = new ItemStackHandler(7) {
				public void onContentsChanged(int slot) {
					slate.getOrCreateTag().put("inventory", this.serializeNBT());
				}
			};
			CompoundTag nbt = slate.getOrCreateTagElement("inventory");
			if (!nbt.isEmpty())
				inventory.deserializeNBT(nbt);
			Misc.giveItemToPlayer(inventory.getStackInSlot(5), player);
			inventory.setStackInSlot(5, ItemStack.EMPTY);
			Misc.giveItemToPlayer(inventory.getStackInSlot(6), player);
			inventory.setStackInSlot(6, ItemStack.EMPTY);
		}
		return InteractionResultHolder.success(player.getItemInHand(hand));
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged || !ItemStack.isSameItem(oldStack, newStack);
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
		ItemStack heldItem = player.getMainHandItem();
		return new SlateMenu(id, inv, heldItem);
	}

	@Override
	public Component getDisplayName() {
		return getDescription();
	}
}
