package com.rekindled.embers.api.filter;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public interface IFilter {
	ResourceLocation getType();

	boolean acceptsItem(ItemStack stack);

	default boolean acceptsItem(ItemStack stack, IItemHandler handler) {
		return acceptsItem(stack);
	}

	String formatFilter();

	CompoundTag writeToNBT(CompoundTag tag);

	void readFromNBT(CompoundTag tag);
}
