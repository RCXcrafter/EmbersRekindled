package com.rekindled.embers.recipe;

import com.rekindled.embers.RegistryManager;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public interface IStampingRecipe extends Recipe<StampingContext> {

	public ItemStack getOutput(RecipeWrapper context);

	@Override
	public default ItemStack getToastSymbol() {
		return new ItemStack(RegistryManager.STAMPER_ITEM.get());
	}

	@Override
	public default RecipeType<?> getType() {
		return RegistryManager.STAMPING.get();
	}

	@Override
	public default ItemStack getResultItem(RegistryAccess registry) {
		return getResultItem();
	}

	public ItemStack getResultItem();

	public FluidIngredient getDisplayInputFluid();

	public Ingredient getDisplayInput();

	public Ingredient getDisplayStamp();

	@Override
	@Deprecated
	public default boolean canCraftInDimensions(int width, int height) {
		return true;
	}
}
