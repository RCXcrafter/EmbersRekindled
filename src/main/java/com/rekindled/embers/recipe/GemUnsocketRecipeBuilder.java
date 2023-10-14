package com.rekindled.embers.recipe;

import java.util.function.Consumer;

import com.google.gson.JsonObject;
import com.rekindled.embers.RegistryManager;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class GemUnsocketRecipeBuilder {

	public ResourceLocation id;

	public static GemUnsocketRecipeBuilder create(ResourceLocation id) {
		GemUnsocketRecipeBuilder builder = new GemUnsocketRecipeBuilder();
		builder.id = id;
		return builder;
	}

	public GemUnsocketRecipeBuilder folder(String folder) {
		this.id = new ResourceLocation(id.getNamespace(), folder + "/" + id.getPath());
		return this;
	}

	public GemUnsocketRecipe build() {
		return new GemUnsocketRecipe(id);
	}

	public void save(Consumer<FinishedRecipe> consumer) {
		consumer.accept(new Finished(build()));
	}

	public static class Finished implements FinishedRecipe {

		public final GemUnsocketRecipe recipe;

		public Finished(GemUnsocketRecipe recipe) {
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
			return RegistryManager.GEM_UNSOCKET_SERIALIZER.get();
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
