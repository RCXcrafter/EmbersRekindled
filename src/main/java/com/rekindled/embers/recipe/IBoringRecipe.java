package com.rekindled.embers.recipe;

import java.util.Collection;
import java.util.List;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.util.WeightedItemStack;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public interface IBoringRecipe extends Recipe<BoringContext> {

	public WeightedItemStack getOutput(BoringContext context);

	@Override
	public default ItemStack getToastSymbol() {
		return new ItemStack(RegistryManager.EMBER_BORE_ITEM.get());
	}

	@Override
	public default RecipeType<?> getType() {
		return RegistryManager.BORING.get();
	}

	public int getMinHeight();

	public int getMaxHeight();

	public Collection<ResourceLocation> getDimensions();

	public Collection<ResourceLocation> getBiomes();

	public double getChance();

	public WeightedItemStack getDisplayOutput();

	public List<ItemStack> getDisplayInput();

	@Override
	@Deprecated
	public default ItemStack assemble(BoringContext context, RegistryAccess registry) {
		return ItemStack.EMPTY;
	}

	@Override
	@Deprecated
	public default boolean canCraftInDimensions(int width, int height) {
		return true;
	}
}
