package com.rekindled.embers.recipe;

import com.rekindled.embers.RegistryManager;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;

public interface IMeltingRecipe extends Recipe<Container> {

	public FluidStack getOutput(Container context);

	public FluidStack getBonus();

	public FluidStack process(Container context);

	@Override
	public default ItemStack getToastSymbol() {
		return new ItemStack(RegistryManager.MELTER_ITEM.get());
	}

	@Override
	public default RecipeType<?> getType() {
		return RegistryManager.MELTING.get();
	}

	public FluidStack getDisplayOutput();

	public Ingredient getDisplayInput();

	@Override
	@Deprecated
	public default ItemStack getResultItem(RegistryAccess registry) {
		return ItemStack.EMPTY;
	}

	@Override
	@Deprecated
	public default ItemStack assemble(Container context, RegistryAccess registry) {
		return ItemStack.EMPTY;
	}

	@Override
	@Deprecated
	public default boolean canCraftInDimensions(int width, int height) {
		return true;
	}
}
