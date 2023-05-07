package com.rekindled.embers.item;

import org.jetbrains.annotations.Nullable;

import com.rekindled.embers.api.item.IHeldEmberCell;
import com.rekindled.embers.api.item.IInventoryEmberCell;
import com.rekindled.embers.power.DefaultEmberItemCapability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class EmberJarItem extends EmberStorageItem {

	public static final double CAPACITY = 2000.0;

	public EmberJarItem(Properties properties) {
		super(properties);
	}

	@Override
	public double getCapacity() {
		return CAPACITY;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return new EmberJarCapability(stack, getCapacity());
	}

	public static class EmberJarCapability extends DefaultEmberItemCapability implements IInventoryEmberCell, IHeldEmberCell {
		public EmberJarCapability(ItemStack stack, double capacity) {
			super(stack, capacity);
		}
	}
}
