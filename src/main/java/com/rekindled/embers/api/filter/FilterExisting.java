package com.rekindled.embers.api.filter;

import com.rekindled.embers.Embers;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class FilterExisting implements IFilter {
	public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(Embers.MODID, "existing");

	@Override
	public ResourceLocation getType() {
		return RESOURCE_LOCATION;
	}

	@Override
	public boolean acceptsItem(ItemStack stack) {
		return false;
	}

	@Override
	public boolean acceptsItem(ItemStack stack, IItemHandler itemHandler) {
		if(itemHandler != null)
			for (int i = 0; i < itemHandler.getSlots(); i++) {
				ItemStack slotStack = itemHandler.getStackInSlot(i);
				if (ItemHandlerHelper.canItemStacksStack(slotStack, stack))
					return true;
			}
		return false;
	}

	@Override
	public String formatFilter() {
		return I18n.get(Embers.MODID + ".filter.existing");
	}

	@Override
	public CompoundTag writeToNBT(CompoundTag tag) {
		tag.putString("type",getType().toString());
		return tag;
	}

	@Override
	public void readFromNBT(CompoundTag tag) {
	}
}
