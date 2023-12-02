package com.rekindled.embers.recipe;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.rekindled.embers.util.Misc;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class AnvilRepairRecipe implements IDawnstoneAnvilRecipe, IVisuallySplitRecipe<IDawnstoneAnvilRecipe> {

	public static final Serializer SERIALIZER = new Serializer(); 

	public final ResourceLocation id;

	public static List<IDawnstoneAnvilRecipe> visualRecipes = new ArrayList<IDawnstoneAnvilRecipe>();

	public AnvilRepairRecipe(ResourceLocation id) {
		this.id = id;
	}

	@Override
	public boolean matches(Container context, Level pLevel) {
		ItemStack tool = context.getItem(0);
		return tool.isRepairable() && tool.isDamaged() && tool.getItem().isValidRepairItem(tool, context.getItem(1));
	}

	@Override
	public ItemStack assemble(Container context, RegistryAccess pRegistryAccess) {
		ItemStack tool = context.getItem(0).copy();
		ItemStack material = context.getItem(1);
		if (tool.isRepairable() && tool.isDamaged() && tool.getItem().isValidRepairItem(tool, material)) {
			tool.setDamageValue(Math.max(0, tool.getDamageValue() - tool.getMaxDamage()));
		}
		return tool;
	}

	@Override
	public ItemStack getOutput(Container context) {
		ItemStack result = context.getItem(0).copy();
		result.setDamageValue(Math.max(0, result.getDamageValue() - result.getMaxDamage()));
		return result;
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
				visualRecipes.add(new AnvilDisplayRecipe(id, List.of(toolStack), List.of(brokenTool), repairMaterial));
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

	public static class Serializer implements RecipeSerializer<AnvilRepairRecipe> {

		@Override
		public AnvilRepairRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			return new AnvilRepairRecipe(recipeId);
		}

		@Override
		public @Nullable AnvilRepairRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			return new AnvilRepairRecipe(recipeId);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, AnvilRepairRecipe recipe) {
		}
	}
}
