package com.rekindled.embers.api.upgrades;

import java.util.List;

import com.rekindled.embers.api.event.UpgradeEvent;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;

public interface IUpgradeUtil {
	List<IUpgradeProvider> getUpgrades(Level world, BlockPos pos, Direction[] facings);

	void getUpgrades(Level world, BlockPos pos, Direction[] facings, List<IUpgradeProvider> upgrades);

	void collectUpgrades(Level world, BlockPos pos, Direction side, List<IUpgradeProvider> upgrades);

	void verifyUpgrades(BlockEntity tile, List<IUpgradeProvider> list);

	int getWorkTime(BlockEntity tile, int time, List<IUpgradeProvider> list);

	double getTotalSpeedModifier(BlockEntity tile, List<IUpgradeProvider> list);

	boolean doTick(BlockEntity tile, List<IUpgradeProvider> list);

	boolean doWork(BlockEntity tile, List<IUpgradeProvider> list);

	double getTotalEmberConsumption(BlockEntity tile, double ember, List<IUpgradeProvider> list);

	double getTotalEmberProduction(BlockEntity tile, double ember, List<IUpgradeProvider> list);

	void transformOutput(BlockEntity tile, List<ItemStack> outputs, List<IUpgradeProvider> list);

	FluidStack transformOutput(BlockEntity tile, FluidStack output, List<IUpgradeProvider> list);

	boolean getOtherParameter(BlockEntity tile, String type, boolean initial, List<IUpgradeProvider> list);

	double getOtherParameter(BlockEntity tile, String type, double initial, List<IUpgradeProvider> list);

	int getOtherParameter(BlockEntity tile, String type, int initial, List<IUpgradeProvider> list);

	String getOtherParameter(BlockEntity tile, String type, String initial, List<IUpgradeProvider> list);

	<T> T getOtherParameter(BlockEntity tile, String type, T initial, List<IUpgradeProvider> list);

	void throwEvent(BlockEntity tile, UpgradeEvent event, List<IUpgradeProvider> list);
}
