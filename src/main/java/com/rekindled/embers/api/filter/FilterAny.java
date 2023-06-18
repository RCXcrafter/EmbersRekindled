package com.rekindled.embers.api.filter;

import com.rekindled.embers.Embers;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class FilterAny implements IFilter {
	public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(Embers.MODID, "any");

	@Override
	public ResourceLocation getType() {
		return RESOURCE_LOCATION;
	}

	@Override
	public boolean acceptsItem(ItemStack stack) {
		return true;
	}

	@Override
	public String formatFilter() {
		return I18n.get(Embers.MODID + ".filter.any");
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
