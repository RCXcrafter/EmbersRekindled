package com.rekindled.embers.recipe;

import java.util.List;

import com.rekindled.embers.RegistryManager;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public interface IDawnstoneAnvilRecipe extends Recipe<Container> {

	public List<ItemStack> getOutput(Container context);

	@Override
	public default ItemStack getToastSymbol() {
		return new ItemStack(RegistryManager.DAWNSTONE_ANVIL_ITEM.get());
	}

	@Override
	public default RecipeType<?> getType() {
		return RegistryManager.DAWNSTONE_ANVIL_RECIPE.get();
	}

	public List<ItemStack> getDisplayInputBottom();

	public List<ItemStack> getDisplayInputTop();

	public List<ItemStack> getDisplayOutput();


	@Override
	@Deprecated
	public default ItemStack assemble(Container context, RegistryAccess pRegistryAccess) {
		return ItemStack.EMPTY;
	}

	@Override
	@Deprecated
	public default ItemStack getResultItem(RegistryAccess pRegistryAccess) {
		return ItemStack.EMPTY;
	}

	@Override
	@Deprecated
	public default boolean canCraftInDimensions(int width, int height) {
		return true;
	}
}
