package com.rekindled.embers.recipe;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.rekindled.embers.api.augment.AugmentUtil;
import com.rekindled.embers.util.Misc;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class AnvilBreakdownRecipe implements IDawnstoneAnvilRecipe, IVisuallySplitRecipe<IDawnstoneAnvilRecipe> {

	public static final Serializer SERIALIZER = new Serializer(); 

	public final ResourceLocation id;

	public static List<IDawnstoneAnvilRecipe> visualRecipes = new ArrayList<IDawnstoneAnvilRecipe>();

	public AnvilBreakdownRecipe(ResourceLocation id) {
		this.id = id;
	}

	@Override
	public boolean matches(Container context, Level pLevel) {
		ItemStack tool = context.getItem(0);
		return tool.isRepairable() && context.getItem(1).isEmpty() && !AugmentUtil.hasHeat(tool);
	}

	@Override
	public List<ItemStack> getOutput(Container context) {
		Ingredient repairMaterial = Misc.getRepairIngredient(context.getItem(0).getItem());
		if (!repairMaterial.isEmpty())
			return List.of(Misc.getPreferredItem(repairMaterial.getItems()));
		return List.of();
	}

	@Override
	public List<IDawnstoneAnvilRecipe> getVisualRecipes() {
		visualRecipes.clear();
		for (Holder<Item> holder : BuiltInRegistries.ITEM.asHolderIdMap()) {
			Ingredient repairMaterial = Misc.getRepairIngredient(holder.get());
			ItemStack toolStack = new ItemStack(holder.get());
			if (!repairMaterial.isEmpty() && toolStack.isRepairable()) {
				ItemStack brokenTool = toolStack.copy();
				brokenTool.setDamageValue(brokenTool.getMaxDamage() / 2);
				visualRecipes.add(new AnvilDisplayRecipe(id, List.of(Misc.getPreferredItem(repairMaterial.getItems())), List.of(brokenTool), Ingredient.EMPTY));
			}
		}
		return visualRecipes;
	}

	@Override
	public List<ItemStack> getDisplayInputBottom() {
		return List.of();
	}

	@Override
	public List<ItemStack> getDisplayInputTop() {
		return List.of();
	}

	@Override
	public List<ItemStack> getDisplayOutput() {
		return List.of();
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	public static class Serializer implements RecipeSerializer<AnvilBreakdownRecipe> {

		@Override
		public AnvilBreakdownRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			return new AnvilBreakdownRecipe(recipeId);
		}

		@Override
		public @Nullable AnvilBreakdownRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			return new AnvilBreakdownRecipe(recipeId);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, AnvilBreakdownRecipe recipe) {
		}
	}
}
