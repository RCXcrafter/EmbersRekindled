package com.rekindled.embers.recipe;

import java.util.ArrayList;
import java.util.List;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.misc.AlchemyResult;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public interface IAlchemyRecipe extends Recipe<AlchemyContext> {

	public ArrayList<Ingredient> getCode(long seed);

	public boolean matchesCorrect(AlchemyContext context, Level pLevel);

	public AlchemyResult getResult(AlchemyContext context);

	@Override
	public default ItemStack getToastSymbol() {
		return new ItemStack(RegistryManager.ALCHEMY_TABLET_ITEM.get());
	}

	@Override
	public default RecipeType<?> getType() {
		return RegistryManager.ALCHEMY.get();
	}

	@Override
	public default ItemStack getResultItem(RegistryAccess registry) {
		return getResultItem();
	}

	public Ingredient getCenterInput();

	public List<Ingredient> getInputs();

	public List<Ingredient> getAspects();

	public ItemStack getResultItem();

	public ItemStack getfailureItem();

	@Override
	@Deprecated
	public default boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	public static class PedestalContents {
		public ItemStack aspect;
		public ItemStack input;

		public PedestalContents(ItemStack aspect, ItemStack input) {
			this.aspect = aspect;
			this.input = input;
		}
	}
}
