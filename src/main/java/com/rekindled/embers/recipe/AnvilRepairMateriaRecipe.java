package com.rekindled.embers.recipe;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.datagen.EmbersItemTags;

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

public class AnvilRepairMateriaRecipe implements IDawnstoneAnvilRecipe, IVisuallySplitRecipe<IDawnstoneAnvilRecipe> {

	public static final Serializer SERIALIZER = new Serializer(); 

	public final ResourceLocation id;

	public static List<IDawnstoneAnvilRecipe> visualRecipes = new ArrayList<IDawnstoneAnvilRecipe>();
	Ingredient materia = Ingredient.of(RegistryManager.ISOLATED_MATERIA.get());
	Ingredient blacklist = Ingredient.of(EmbersItemTags.MATERIA_BLACKLIST);

	public AnvilRepairMateriaRecipe(ResourceLocation id) {
		this.id = id;
	}

	@Override
	public boolean matches(Container context, Level pLevel) {
		ItemStack tool = context.getItem(0);
		return tool.isRepairable() && tool.isDamaged() && !blacklist.test(tool) && materia.test(context.getItem(1));
	}

	@Override
	public ItemStack assemble(Container context, RegistryAccess pRegistryAccess) {
		ItemStack tool = context.getItem(0).copy();
		ItemStack material = context.getItem(1);
		if (tool.isRepairable() && tool.isDamaged() && !blacklist.test(tool) && materia.test(material)) {
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
			ItemStack toolStack = new ItemStack(holder.get());
			if (toolStack.isRepairable() && !blacklist.test(toolStack)) {
				ItemStack brokenTool = toolStack.copy();
				brokenTool.setDamageValue(brokenTool.getMaxDamage() / 2);
				visualRecipes.add(new AnvilDisplayRecipe(id, List.of(toolStack), List.of(brokenTool), materia));
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

	public static class Serializer implements RecipeSerializer<AnvilRepairMateriaRecipe> {

		@Override
		public AnvilRepairMateriaRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			return new AnvilRepairMateriaRecipe(recipeId);
		}

		@Override
		public @Nullable AnvilRepairMateriaRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			return new AnvilRepairMateriaRecipe(recipeId);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, AnvilRepairMateriaRecipe recipe) {
		}
	}
}
