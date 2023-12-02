package com.rekindled.embers.recipe;

import com.rekindled.embers.RegistryManager;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public interface ICatalysisCombustionRecipe extends Recipe<CatalysisCombustionContext> {

	public int getBurnTIme(CatalysisCombustionContext context);

	public double getmultiplier(CatalysisCombustionContext context);

	public int process(CatalysisCombustionContext context);

	@Override
	public default ItemStack getToastSymbol() {
		return new ItemStack(RegistryManager.IGNEM_REACTOR_ITEM.get());
	}

	@Override
	public default RecipeType<?> getType() {
		return RegistryManager.CATALYSIS_COMBUSTION.get();
	}

	public Ingredient getDisplayInput();

	public Ingredient getDisplayMachine();

	public int getDisplayTime();

	public double getDisplayMultiplier();

	@Override
	@Deprecated
	public default ItemStack getResultItem(RegistryAccess registry) {
		return ItemStack.EMPTY;
	}

	@Override
	@Deprecated
	public default ItemStack assemble(CatalysisCombustionContext context, RegistryAccess registry) {
		return ItemStack.EMPTY;
	}

	@Override
	@Deprecated
	public default boolean canCraftInDimensions(int width, int height) {
		return true;
	}
}
