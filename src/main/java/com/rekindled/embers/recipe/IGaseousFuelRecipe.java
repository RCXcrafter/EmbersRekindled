package com.rekindled.embers.recipe;

import com.rekindled.embers.RegistryManager;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public interface IGaseousFuelRecipe extends Recipe<FluidHandlerContext> {

	public int getBurnTime(FluidHandlerContext context);

	public double getPowerMultiplier(FluidHandlerContext context);

	public int process(FluidHandlerContext context, int amount);

	@Override
	public default ItemStack getToastSymbol() {
		return new ItemStack(RegistryManager.WILDFIRE_STIRLING_ITEM.get());
	}

	@Override
	public default RecipeType<?> getType() {
		return RegistryManager.GASEOUS_FUEL.get();
	}

	public FluidIngredient getDisplayInput();

	public int getDisplayBurnTime();

	@Override
	@Deprecated
	public default ItemStack assemble(FluidHandlerContext context, RegistryAccess registry) {
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
