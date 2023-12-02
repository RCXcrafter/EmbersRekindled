package com.rekindled.embers.recipe;

import com.rekindled.embers.RegistryManager;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public interface IEmberActivationRecipe extends Recipe<Container> {

	public int getOutput(Container context);

	public int process(Container context);

	@Override
	public default ItemStack getToastSymbol() {
		return new ItemStack(RegistryManager.EMBER_ACTIVATOR_ITEM.get());
	}

	@Override
	public default RecipeType<?> getType() {
		return RegistryManager.EMBER_ACTIVATION.get();
	}

	public Ingredient getDisplayInput();

	public int getDisplayOutput();

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
