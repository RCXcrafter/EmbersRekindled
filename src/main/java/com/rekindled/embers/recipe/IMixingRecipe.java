package com.rekindled.embers.recipe;

import java.util.ArrayList;

import com.rekindled.embers.RegistryManager;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;

public interface IMixingRecipe extends Recipe<MixingContext> {

	public FluidStack getOutput(MixingContext context);

	public FluidStack process(MixingContext context);

	@Override
	public default ItemStack getToastSymbol() {
		return new ItemStack(RegistryManager.MIXER_CENTRIFUGE_ITEM.get());
	}

	@Override
	public default RecipeType<?> getType() {
		return RegistryManager.MIXING.get();
	}

	public ArrayList<FluidIngredient> getDisplayInputFluids();

	public FluidStack getDisplayOutput();

	@Override
	@Deprecated
	public default ItemStack assemble(MixingContext context, RegistryAccess registry) {
		return ItemStack.EMPTY;
	}

	@Override
	@Deprecated
	public default ItemStack getResultItem(RegistryAccess registry) {
		return ItemStack.EMPTY;
	}

	@Override
	@Deprecated
	public default boolean canCraftInDimensions(int width, int height) {
		return true;
	}
}
