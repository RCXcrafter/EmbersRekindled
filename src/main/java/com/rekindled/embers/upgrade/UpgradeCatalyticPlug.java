package com.rekindled.embers.upgrade;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.event.DialInformationEvent;
import com.rekindled.embers.api.event.UpgradeEvent;
import com.rekindled.embers.api.upgrades.UpgradeContext;
import com.rekindled.embers.block.EmberDialBlock;
import com.rekindled.embers.blockentity.CatalyticPlugBlockEntity;
import com.rekindled.embers.util.DecimalFormats;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class UpgradeCatalyticPlug extends DefaultUpgradeProvider {

	private static HashSet<Class<? extends BlockEntity>> blacklist = new HashSet<>();

	public static void registerBlacklistedTile(Class<? extends BlockEntity> tile) {
		blacklist.add(tile);
	}

	public UpgradeCatalyticPlug(BlockEntity tile) {
		super(new ResourceLocation(Embers.MODID, "catalytic_plug"), tile);
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
		return hasCatalyst() ? ember * getMultiplier(distance, count) : ember; //+200% if catalyst available
	}

	@Override
	public double getSpeed(BlockEntity tile, double speed, int distance, int count) {
		return hasCatalyst() ? speed * getMultiplier(distance, count) : speed; //+200% if catalyst available
	}

	@Override
	public boolean doWork(BlockEntity tile, List<UpgradeContext> upgrades, int distance, int count) {
		if (hasCatalyst() && this.tile instanceof CatalyticPlugBlockEntity) {
			depleteCatalyst(1);
			((CatalyticPlugBlockEntity) this.tile).setActive(20);
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

	@Override
	@OnlyIn(Dist.CLIENT)
	public void throwEvent(BlockEntity tile, List<UpgradeContext> upgrades, UpgradeEvent event, int distance, int count) {
		if (event instanceof DialInformationEvent dialEvent && EmberDialBlock.DIAL_TYPE.equals(dialEvent.getDialType())) {
			double multiplier = 1.0;
			boolean first = true;
			for (UpgradeContext upgrade : upgrades) {
				if (upgrade.upgrade() instanceof UpgradeCatalyticPlug plug) {
					if (first) {
						if (plug != this)
							return;
						first = false;
					}
					multiplier = plug.getSpeed(tile, multiplier, upgrade.distance(), upgrade.count());
				}
			}
			DecimalFormat multiplierFormat = DecimalFormats.getDecimalFormat(Embers.MODID + ".decimal_format.speed_multiplier");
			dialEvent.getInformation().add(I18n.get(Embers.MODID + ".tooltip.upgrade.catalytic_plug", multiplierFormat.format(multiplier)));
		}
	}
}
