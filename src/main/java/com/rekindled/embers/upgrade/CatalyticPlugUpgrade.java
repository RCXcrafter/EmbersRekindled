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
import com.rekindled.embers.recipe.FluidHandlerContext;
import com.rekindled.embers.util.DecimalFormats;
import com.rekindled.embers.util.Misc;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CatalyticPlugUpgrade extends DefaultUpgradeProvider {

	private static HashSet<Class<? extends BlockEntity>> blacklist = new HashSet<>();

	public static void registerBlacklistedTile(Class<? extends BlockEntity> tile) {
		blacklist.add(tile);
	}

	public CatalyticPlugUpgrade(BlockEntity tile) {
		super(new ResourceLocation(Embers.MODID, "catalytic_plug"), tile);
	}

	public static double getMultiplier(double multiplier, int distance, int count) {
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
		return ember * getMultiplier(getCatalystMultiplier(), distance, count);
	}

	@Override
	public double getSpeed(BlockEntity tile, double speed, int distance, int count) {
		return speed * getMultiplier(getCatalystMultiplier(), distance, count);
	}

	@Override
	public double getOtherParameter(BlockEntity tile, String type, double value, int distance, int count) {
		if (type.equals("fuel_consumption"))
			return value * getMultiplier(getCatalystMultiplier(), distance, count);
		return value;
	}

	@Override
	public boolean doWork(BlockEntity tile, List<UpgradeContext> upgrades, int distance, int count) {
		if (getCatalystMultiplier() != 1.0 && this.tile instanceof CatalyticPlugBlockEntity) {
			depleteCatalyst(1);
			((CatalyticPlugBlockEntity) this.tile).setActive(20);
		}
		return false; //No cancel
	}

	private double getCatalystMultiplier() {
		if (this.tile instanceof CatalyticPlugBlockEntity plug) {
			FluidHandlerContext context = new FluidHandlerContext(plug.tank);
			if (plug.burnTime <= 0 || plug.cachedRecipe == null)
				plug.cachedRecipe = Misc.getRecipe(plug.cachedRecipe, RegistryManager.GASEOUS_FUEL.get(), context, plug.getLevel());
			return plug.cachedRecipe == null ? 1.0 : plug.cachedRecipe.getPowerMultiplier(context);
		}
		return 1.0;
	}

	private void depleteCatalyst(int amt) {
		if (this.tile instanceof CatalyticPlugBlockEntity plug) {
			plug.burnTime -= amt;
			if (plug.burnTime < 0) {
				FluidHandlerContext context = new FluidHandlerContext(plug.tank);
				plug.cachedRecipe = Misc.getRecipe(plug.cachedRecipe, RegistryManager.GASEOUS_FUEL.get(), context, plug.getLevel());
				while (plug.burnTime < 0 && plug.cachedRecipe != null && plug.cachedRecipe.matches(context, plug.getLevel())) {
					plug.burnTime += plug.cachedRecipe.process(context, 1);
				}
				if (plug.burnTime < 0)
					plug.burnTime = 0;
			}
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void throwEvent(BlockEntity tile, List<UpgradeContext> upgrades, UpgradeEvent event, int distance, int count) {
		if (event instanceof DialInformationEvent dialEvent && EmberDialBlock.DIAL_TYPE.equals(dialEvent.getDialType())) {
			double multiplier = 1.0;
			boolean first = true;
			for (UpgradeContext upgrade : upgrades) {
				if (upgrade.upgrade() instanceof CatalyticPlugUpgrade plug) {
					if (first) {
						if (plug != this)
							return;
						first = false;
					}
					multiplier = plug.getSpeed(tile, multiplier, upgrade.distance(), upgrade.count());
				}
			}
			DecimalFormat multiplierFormat = DecimalFormats.getDecimalFormat(Embers.MODID + ".decimal_format.speed_multiplier");
			dialEvent.getInformation().add(Component.translatable(Embers.MODID + ".tooltip.upgrade.catalytic_plug", multiplierFormat.format(multiplier)));
		}
	}
}
