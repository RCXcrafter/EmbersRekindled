package com.rekindled.embers.recipe;

import java.util.function.Consumer;

import com.google.gson.JsonObject;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class GenericRecipeBuilder {

	public Recipe<?> recipe;

	public static GenericRecipeBuilder create(Recipe<?> recipe) {
		GenericRecipeBuilder builder = new GenericRecipeBuilder();
		builder.recipe = recipe;
		return builder;
	}

	public void save(Consumer<FinishedRecipe> consumer) {
		consumer.accept(new Finished(recipe));
	}

	public static class Finished implements FinishedRecipe {

		public final Recipe<?> recipe;

		public Finished(Recipe<?> recipe) {
			this.recipe = recipe;
		}

		@Override
		public void serializeRecipeData(JsonObject json) {
		}

		@Override
		public ResourceLocation getId() {
			return recipe.getId();
		}

		@Override
		public RecipeSerializer<?> getType() {
			return recipe.getSerializer();
		}

		@Override
		public JsonObject serializeAdvancement() {
			return null;
		}

		@Override
		public ResourceLocation getAdvancementId() {
			return null;
		}
	}
}
