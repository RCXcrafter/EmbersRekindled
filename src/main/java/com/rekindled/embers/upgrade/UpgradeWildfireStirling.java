package com.rekindled.embers.upgrade;

import java.util.HashSet;
import java.util.List;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.upgrades.IUpgradeProvider;
import com.rekindled.embers.blockentity.WildfireStirlingBlockEntity;

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
		super("stirling", tile);
	}

	@Override
	public int getLimit(BlockEntity tile) {
		return blacklist.contains(tile.getClass()) ? 0 : 2;
	}

	@Override
	public double transformEmberConsumption(BlockEntity tile, double ember) {
		return hasCatalyst() ? ember * 0.5 : ember; //-50% if catalyst available
	}

	@Override
	public boolean doWork(BlockEntity tile, List<IUpgradeProvider> upgrades) {
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
