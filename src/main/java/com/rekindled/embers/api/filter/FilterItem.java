package com.rekindled.embers.api.filter;

import com.rekindled.embers.Embers;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class FilterItem implements IFilter {
	public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(Embers.MODID, "item");

	private ItemStack filterItem = ItemStack.EMPTY;

	public FilterItem(ItemStack filterItem) {
		this.filterItem = filterItem;
	}

	public FilterItem(CompoundTag tag) {
		readFromNBT(tag);
	}

	@Override
	public ResourceLocation getType() {
		return RESOURCE_LOCATION;
	}

	@Override
	public boolean acceptsItem(ItemStack stack) {
		return filterItem.getItem() == stack.getItem() && filterItem.getDamageValue() == stack.getDamageValue();
	}

	@Override
	public String formatFilter() {
		return I18n.get(Embers.MODID + ".filter.strict", filterItem.getDisplayName());
	}

	@Override
	public CompoundTag writeToNBT(CompoundTag tag) {
		tag.putString("type",getType().toString());
		tag.put("filterStack",filterItem.serializeNBT());
		return tag;
	}

	@Override
	public void readFromNBT(CompoundTag tag) {
		filterItem = ItemStack.of(tag.getCompound("filterStack"));
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof FilterItem)
			return equals((FilterItem) obj);
		return super.equals(obj);
	}

	private boolean equals(FilterItem other) {
		return ItemStack.isSame(filterItem, other.filterItem);
	}
}
