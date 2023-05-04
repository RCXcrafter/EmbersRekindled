package com.rekindled.embers.upgrade;

import com.rekindled.embers.api.event.MachineRecipeEvent;
import com.rekindled.embers.api.event.UpgradeEvent;
import com.rekindled.embers.blockentity.GeologicSeparatorBlockEntity;
import com.rekindled.embers.recipe.MeltingRecipe;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class UpgradeGeologicSeparator extends DefaultUpgradeProvider {

	public UpgradeGeologicSeparator(BlockEntity tile) {
		super("geo_separator", tile);
	}

	@Override
	public void throwEvent(BlockEntity tile, UpgradeEvent event) {
		if(event instanceof MachineRecipeEvent.Success) {
			Object recipe = ((MachineRecipeEvent<?>) event).getRecipe();
			if(recipe instanceof MeltingRecipe) {
				FluidStack bonus = ((MeltingRecipe) recipe).getBonus();
				if (!bonus.isEmpty() && this.tile instanceof GeologicSeparatorBlockEntity) {
					IFluidHandler fluidHandler = this.tile.getCapability(ForgeCapabilities.FLUID_HANDLER, null).orElse(null);
					if (fluidHandler != null)
						fluidHandler.fill(bonus.copy(), FluidAction.EXECUTE);
				}
			}
		}
	}
}
