package com.rekindled.embers.api.power;

import com.rekindled.embers.Embers;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public interface IEmberCapability extends ICapabilitySerializable<CompoundTag> {

	public static final String EMBER_CAPACITY = Embers.MODID + ":ember_capacity";
	public static final String EMBER = Embers.MODID + ":ember";

	double getEmber();
	double getEmberCapacity();
	void setEmber(double value);
	void setEmberCapacity(double value);
	double addAmount(double value, boolean doAdd);
	double removeAmount(double value, boolean doRemove);
	void writeToNBT(CompoundTag nbt);
	void onContentsChanged();
	void invalidate();
	default boolean acceptsVolatile() {
		return false;
	}
}
