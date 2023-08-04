package com.rekindled.embers.api.filter;

import java.util.List;
import java.util.Objects;

import com.rekindled.embers.Embers;
import com.rekindled.embers.util.FilterUtil;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class FilterSieve implements IFilter {
	public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(Embers.MODID, "sieve");

	private ItemStack stack1;
	private ItemStack stack2;
	private int offset;
	private EnumFilterSetting setting;
	private boolean inverted;
	private IFilterComparator comparator;

	public FilterSieve(CompoundTag tag) {
		readFromNBT(tag);
	}

	public FilterSieve(ItemStack stack1, ItemStack stack2, int offset, EnumFilterSetting setting, boolean inverted) {
		this.stack1 = stack1;
		this.stack2 = stack2;
		this.offset = offset;
		this.setting = setting;
		this.inverted = inverted;
		findComparator();
	}

	private void findComparator() {
		if(stack1.isEmpty() && stack2.isEmpty())
			comparator = FilterUtil.ANY;
		else {
			List<IFilterComparator> comparators = FilterUtil.getComparators(stack1, stack2);
			comparator = comparators.get(offset % comparators.size());
		}
	}

	@Override
	public ResourceLocation getType() {
		return RESOURCE_LOCATION;
	}

	@Override
	public boolean acceptsItem(ItemStack stack) {
		return comparator.isBetween(stack1, stack2, stack, setting) != inverted;
	}

	@Override
	public String formatFilter() {
		if(comparator == null)
			return "INVALID COMPARATOR";
		return comparator.format(stack1,stack2,setting,inverted);
	}

	@Override
	public CompoundTag writeToNBT(CompoundTag tag) {
		tag.putString("type",getType().toString());
		tag.put("stack1",stack1.serializeNBT());
		tag.put("stack2",stack2.serializeNBT());
		tag.putInt("offset",offset);
		tag.putInt("setting",setting.ordinal());
		tag.putBoolean("inverted",inverted);
		tag.putString("comparator", comparator.getName());
		return tag;
	}

	@Override
	public void readFromNBT(CompoundTag tag) {
		stack1 = ItemStack.of(tag.getCompound("stack1"));
		stack2 = ItemStack.of(tag.getCompound("stack2"));
		offset = tag.getInt("offset");
		setting = EnumFilterSetting.values()[tag.getInt("setting")];
		inverted = tag.getBoolean("invert");
		if(tag.contains("comparator"))
			comparator = FilterUtil.getComparator(tag.getString("comparator"));
		else
			findComparator();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof FilterSieve)
			return equals((FilterSieve) obj);
		return super.equals(obj);
	}

	private boolean equals(FilterSieve other) {
		return Objects.equals(comparator, other.comparator)
				&& Objects.equals(inverted,other.inverted)
				&& Objects.equals(setting, other.setting)
				&& ItemStack.isSameItemSameTags(stack1, other.stack1)
				&& ItemStack.isSameItemSameTags(stack2, other.stack2);
	}
}
