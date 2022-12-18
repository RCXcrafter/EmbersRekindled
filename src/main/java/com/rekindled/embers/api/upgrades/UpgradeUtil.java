package com.rekindled.embers.api.upgrades;

import java.util.List;

import com.rekindled.embers.api.event.UpgradeEvent;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;

public class UpgradeUtil {
	public static IUpgradeUtil IMPL;

	public static List<IUpgradeProvider> getUpgrades(Level world, BlockPos pos, Direction[] facings) {
		return IMPL.getUpgrades(world, pos, facings);
	}

	public static void getUpgrades(Level world, BlockPos pos, Direction[] facings, List<IUpgradeProvider> upgrades) {
		IMPL.getUpgrades(world, pos, facings, upgrades);
	}

	public static void collectUpgrades(Level world, BlockPos pos, Direction side, List<IUpgradeProvider> upgrades) {
		IMPL.collectUpgrades(world, pos, side, upgrades);
	}

	public static void verifyUpgrades(BlockEntity tile, List<IUpgradeProvider> list) {
		IMPL.verifyUpgrades(tile, list);
	}

	public static double getTotalSpeedModifier(BlockEntity tile, List<IUpgradeProvider> list) {
		return IMPL.getTotalSpeedModifier(tile, list);
	}

	public static int getWorkTime(BlockEntity tile, int time, List<IUpgradeProvider> list) {
		return IMPL.getWorkTime(tile,time,list);
	}

	//DO NOT CALL FROM AN UPGRADE'S doWork METHOD!!
	public static boolean doWork(BlockEntity tile, List<IUpgradeProvider> list) {
		return IMPL.doWork(tile, list);
	}

	//DO NOT CALL FROM AN UPGRADE'S doTick METHOD!!
	public static boolean doTick(BlockEntity tile, List<IUpgradeProvider> list) {
		return IMPL.doTick(tile, list);
	}

	public static double getTotalEmberConsumption(BlockEntity tile, double ember, List<IUpgradeProvider> list) {
		return IMPL.getTotalEmberConsumption(tile, ember, list);
	}

	public static double getTotalEmberProduction(BlockEntity tile, double ember, List<IUpgradeProvider> list) {
		return IMPL.getTotalEmberProduction(tile, ember, list);
	}

	public static void transformOutput(BlockEntity tile, List<ItemStack> outputs, List<IUpgradeProvider> list) {
		IMPL.transformOutput(tile, outputs, list);
	}

	public static FluidStack transformOutput(BlockEntity tile, FluidStack output, List<IUpgradeProvider> list) {
		return IMPL.transformOutput(tile, output, list);
	}

	public static boolean getOtherParameter(BlockEntity tile, String type, boolean initial, List<IUpgradeProvider> list) {
		return IMPL.getOtherParameter(tile, type, initial, list);
	}

	public static double getOtherParameter(BlockEntity tile, String type, double initial, List<IUpgradeProvider> list) {
		return IMPL.getOtherParameter(tile, type, initial, list);
	}

	public static int getOtherParameter(BlockEntity tile, String type, int initial, List<IUpgradeProvider> list) {
		return IMPL.getOtherParameter(tile, type, initial, list);
	}

	public static String getOtherParameter(BlockEntity tile, String type, String initial, List<IUpgradeProvider> list) {
		return IMPL.getOtherParameter(tile, type, initial, list);
	}

	public static <T> T getOtherParameter(BlockEntity tile, String type, T initial, List<IUpgradeProvider> list) {
		return IMPL.getOtherParameter(tile, type, initial, list);
	}

	public static void throwEvent(BlockEntity tile, UpgradeEvent event, List<IUpgradeProvider> list) {
		IMPL.throwEvent(tile,event,list);
	}
}
