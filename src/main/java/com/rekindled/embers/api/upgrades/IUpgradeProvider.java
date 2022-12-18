package com.rekindled.embers.api.upgrades;

import java.util.List;

import com.rekindled.embers.api.event.UpgradeEvent;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;

public interface IUpgradeProvider {
	//The upgrade id
	String getUpgradeId();

	//Determines the order in which upgrades apply
	default int getPriority() {
		return 0;
	}

	//Determines how many of this upgrade can be applied. If the number surpasses this limit, they'll all be discarded!
	default int getLimit(BlockEntity tile) {
		return Integer.MAX_VALUE;
	}

	//The speed modifier that this upgrade provides
	default double getSpeed(BlockEntity tile, double speed) {
		return speed;
	}

	//Called on machine update
	//If this returns true, this call replaces the entire update call.
	default boolean doTick(BlockEntity tile, List<IUpgradeProvider> upgrades) {
		return false;
	}

	//Called if machine is working
	//If this returns true, this call replaces the usual work the machine would do.
	default boolean doWork(BlockEntity tile, List<IUpgradeProvider> upgrades) {
		return false;
	}

	default double transformEmberConsumption(BlockEntity tile, double ember) {
		return ember;
	}

	default double transformEmberProduction(BlockEntity tile, double ember) {
		return ember;
	}

	//Called when machine would output items, allows modification of what items the machine outputs.
	default void transformOutput(BlockEntity tile, List<ItemStack> outputs) {
		//NOOP
	}

	//Called when machine would output fluid, allows modification of what fluid it will output.
	default FluidStack transformOutput(BlockEntity tile, FluidStack output)
	{
		return output;
	}

	default boolean getOtherParameter(BlockEntity tile, String type, boolean value) { return value; }

	default double getOtherParameter(BlockEntity tile, String type, double value) { return value; }

	default int getOtherParameter(BlockEntity tile, String type, int value) { return value; }

	default String getOtherParameter(BlockEntity tile, String type, String value) { return value; }

	default <T> T getOtherParameter(BlockEntity tile, String type, T value) { return value; }

	default void throwEvent(BlockEntity tile, UpgradeEvent event) {}
}
