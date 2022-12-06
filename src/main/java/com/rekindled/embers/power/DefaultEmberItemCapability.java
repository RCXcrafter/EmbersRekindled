package com.rekindled.embers.power;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.power.IEmberCapability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class DefaultEmberItemCapability implements IEmberCapability {
	@Nonnull
	public ItemStack stack;
	public final LazyOptional<?> capOptional;
	double ember = 0;
	double capacity = 0;

	public DefaultEmberItemCapability(@Nonnull ItemStack stack, double capacity) {
		this.stack = stack;
		setEmberCapacity(capacity);
		this.capOptional = LazyOptional.of(() -> this);

		CompoundTag BEnbt = stack.getTagElement("BlockEntityTag");
		if (BEnbt != null) {
			setEmber(BEnbt.getDouble(EMBER));
			setEmberCapacity(BEnbt.getDouble(EMBER_CAPACITY));
		}
	}

	@Override
	public void invalidate() {
		capOptional.invalidate();
	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
		if (capability == EmbersCapabilities.EMBER_CAPABILITY)
			return capOptional.cast();
		return LazyOptional.empty();
	}

	@Override
	public double getEmber() {
		return ember;
	}

	@Override
	public double getEmberCapacity() {
		return capacity;
	}

	@Override
	public void setEmber(double value) {
		ember = value;
		stack.getOrCreateTagElement("ForgeCaps").putDouble(EMBER, value);
	}

	@Override
	public void setEmberCapacity(double value) {
		capacity = value;
		stack.getOrCreateTagElement("ForgeCaps").putDouble(EMBER_CAPACITY, value);
	}

	@Override
	public double addAmount(double value, boolean doAdd) {
		double ember = getEmber();
		double capacity = getEmberCapacity();
		double added = Math.min(capacity - ember, value);
		double newEmber = ember + added;
		if (doAdd) {
			if (newEmber != ember)
				onContentsChanged();
			setEmber(ember + added);
		}
		return added;
	}

	@Override
	public double removeAmount(double value, boolean doRemove) {
		double ember = getEmber();
		double removed = Math.min(ember, value);
		double newEmber = ember - removed;
		if (doRemove) {
			if (newEmber != ember)
				onContentsChanged();
			setEmber(ember - removed);
		}
		return removed;
	}

	@Override
	public void writeToNBT(CompoundTag nbt) {
		nbt.putDouble(EMBER, ember);
		nbt.putDouble(EMBER_CAPACITY, capacity);
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		if (nbt.contains(EMBER))
			ember = nbt.getDouble(EMBER);
		if (nbt.contains(EMBER_CAPACITY))
			capacity = nbt.getDouble(EMBER_CAPACITY);
	}

	@Override
	public void onContentsChanged() {

	}
}
