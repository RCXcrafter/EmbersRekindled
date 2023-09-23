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
	List<UpgradeContext> getUpgrades(Level world, BlockPos pos, Direction[] facings);

	void getUpgrades(Level world, BlockPos pos, Direction[] facings, List<UpgradeContext> upgrades);

	void collectUpgrades(Level world, BlockPos pos, Direction side, List<UpgradeContext> upgrades);

	void collectUpgrades(Level world, BlockPos pos, Direction side, List<UpgradeContext> upgrades, int distanceLeft);

	void verifyUpgrades(BlockEntity tile, List<UpgradeContext> list);

	int getWorkTime(BlockEntity tile, int time, List<UpgradeContext> list);

	double getTotalSpeedModifier(BlockEntity tile, List<UpgradeContext> list);

	boolean doTick(BlockEntity tile, List<UpgradeContext> list);

	boolean doWork(BlockEntity tile, List<UpgradeContext> list);

	double getTotalEmberConsumption(BlockEntity tile, double ember, List<UpgradeContext> list);

	double getTotalEmberProduction(BlockEntity tile, double ember, List<UpgradeContext> list);

	void transformOutput(BlockEntity tile, List<ItemStack> outputs, List<UpgradeContext> list);

	FluidStack transformOutput(BlockEntity tile, FluidStack output, List<UpgradeContext> list);

	boolean getOtherParameter(BlockEntity tile, String type, boolean initial, List<UpgradeContext> list);

	double getOtherParameter(BlockEntity tile, String type, double initial, List<UpgradeContext> list);

	int getOtherParameter(BlockEntity tile, String type, int initial, List<UpgradeContext> list);

	String getOtherParameter(BlockEntity tile, String type, String initial, List<UpgradeContext> list);

	<T> T getOtherParameter(BlockEntity tile, String type, T initial, List<UpgradeContext> list);

	void throwEvent(BlockEntity tile, UpgradeEvent event, List<UpgradeContext> list);
}
