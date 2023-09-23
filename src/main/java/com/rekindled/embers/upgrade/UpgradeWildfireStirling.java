package com.rekindled.embers.upgrade;

import java.util.HashSet;
import java.util.List;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.upgrades.UpgradeContext;
import com.rekindled.embers.blockentity.WildfireStirlingBlockEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class UpgradeWildfireStirling extends DefaultUpgradeProvider {

	private static HashSet<Class<? extends BlockEntity>> blacklist = new HashSet<>();

	public static void registerBlacklistedTile(Class<? extends BlockEntity> tile) {
		blacklist.add(tile);
	}

	public UpgradeWildfireStirling(BlockEntity tile) {
		super(new ResourceLocation(Embers.MODID, "wildfire_stirling"), tile);
	}
	
	public static double getMultiplier(int distance, int count) {
		double multiplier = 2.0;
		if (distance > 1) {
			multiplier = 1.0 + (multiplier - 1.0) / (distance * 0.5);
		}
		if (count > 2) {
			multiplier = 1.0 + (multiplier - 1.0) / (count * 0.4);
		}
		return multiplier;
	}

	@Override
	public int getLimit(BlockEntity tile) {
		return blacklist.contains(tile.getClass()) ? 0 : super.getLimit(tile);
	}

	@Override
	public double transformEmberConsumption(BlockEntity tile, double ember, int distance, int count) {
		return hasCatalyst() ? ember / getMultiplier(distance, count) : ember; //-50% if catalyst available
	}

	@Override
	public boolean doWork(BlockEntity tile, List<UpgradeContext> upgrades, int distance, int count) {
		if(hasCatalyst() && this.tile instanceof WildfireStirlingBlockEntity) {
			depleteCatalyst(1);
			((WildfireStirlingBlockEntity) this.tile).setActive(20);
		}
		return false; //No cancel
	}

	private boolean hasCatalyst() {
		IFluidHandler handler = tile.getCapability(ForgeCapabilities.FLUID_HANDLER, null).orElse(null);
		if (handler != null)
			return !handler.drain(new FluidStack(RegistryManager.STEAM.FLUID.get(), 1), FluidAction.SIMULATE).isEmpty();
		return false;
	}

	private void depleteCatalyst(int amt) {
		IFluidHandler handler = tile.getCapability(ForgeCapabilities.FLUID_HANDLER, null).orElse(null);
		if (handler != null)
			handler.drain(new FluidStack(RegistryManager.STEAM.FLUID.get(), amt), FluidAction.EXECUTE);
	}
}
