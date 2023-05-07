package com.rekindled.embers.item;

import org.jetbrains.annotations.Nullable;

import com.rekindled.embers.api.item.IHeldEmberCell;
import com.rekindled.embers.power.DefaultEmberItemCapability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class EmberCartridgeItem extends EmberStorageItem {

	public static final double CAPACITY = 6000.0;

	public EmberCartridgeItem(Properties properties) {
		super(properties);
	}

	@Override
	public double getCapacity() {
		return CAPACITY;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return new EmberCartridgeCapability(stack, getCapacity());
	}

	public static class EmberCartridgeCapability extends DefaultEmberItemCapability implements IHeldEmberCell {
		public EmberCartridgeCapability(ItemStack stack, double capacity) {
			super(stack, capacity);
		}
	}
}
