package com.rekindled.embers.upgrade;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.event.DialInformationEvent;
import com.rekindled.embers.api.event.UpgradeEvent;
import com.rekindled.embers.api.upgrades.IUpgradeProvider;
import com.rekindled.embers.block.EmberDialBlock;
import com.rekindled.embers.blockentity.CatalyticPlugBlockEntity;
import com.rekindled.embers.util.DecimalFormats;

import net.minecraft.client.resources.language.I18n;
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
		super("catalytic_plug", tile);
	}

	@Override
	public int getLimit(BlockEntity tile) {
		return blacklist.contains(tile.getClass()) ? 0 : 2;
	}

	@Override
	public double transformEmberConsumption(BlockEntity tile, double ember) {
		return hasCatalyst() ? ember * 2.0 : ember; //+200% if catalyst available
	}

	@Override
	public double getSpeed(BlockEntity tile, double speed) {
		return hasCatalyst() ? speed * 2.0 : speed; //+200% if catalyst available
	}

	@Override
	public boolean doWork(BlockEntity tile, List<IUpgradeProvider> upgrades) {
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
	public void throwEvent(BlockEntity tile, UpgradeEvent event) {
		if (event instanceof DialInformationEvent) {
			DialInformationEvent dialEvent = (DialInformationEvent) event;
			if (hasCatalyst() && EmberDialBlock.DIAL_TYPE.equals(dialEvent.getDialType())) {
				double speedModifier = 2.0;
				DecimalFormat multiplierFormat = DecimalFormats.getDecimalFormat(Embers.MODID + ".decimal_format.speed_multiplier");
				dialEvent.getInformation().add(I18n.get(Embers.MODID + ".tooltip.upgrade.catalytic_plug", multiplierFormat.format(speedModifier)));
			}
		}
	}
}
