package com.rekindled.embers.recipe;

import java.util.function.Consumer;

import com.google.gson.JsonObject;
import com.rekindled.embers.RegistryManager;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;

public class MetalCoefficientRecipeBuilder {

	public ResourceLocation id;
	public ResourceLocation blockTag;
	public double coefficient;

	public static MetalCoefficientRecipeBuilder create(ResourceLocation tag) {
		MetalCoefficientRecipeBuilder builder = new MetalCoefficientRecipeBuilder();
		builder.blockTag = tag;
		return builder;
	}

	public static MetalCoefficientRecipeBuilder create(TagKey<Block> tag) {
		MetalCoefficientRecipeBuilder builder = create(tag.location());
		builder.id = tag.location();
		return builder;
	}

	public MetalCoefficientRecipeBuilder id(ResourceLocation id) {
		this.id = id;
		return this;
	}

	public MetalCoefficientRecipeBuilder domain(String domain) {
		this.id = new ResourceLocation(domain, this.id.getPath());
		return this;
	}

	public MetalCoefficientRecipeBuilder folder(String folder) {
		this.id = new ResourceLocation(id.getNamespace(), folder + "/" + id.getPath());
		return this;
	}

	public MetalCoefficientRecipeBuilder coefficient(double coefficient) {
		this.coefficient = coefficient;
		return this;
	}

	public MetalCoefficientRecipe build() {
		return new MetalCoefficientRecipe(id, BlockTags.create(blockTag), coefficient);
	}

	public void save(Consumer<FinishedRecipe> consumer) {
		consumer.accept(new Finished(build()));
	}

	public static class Finished implements FinishedRecipe {

		public final MetalCoefficientRecipe recipe;

		public Finished(MetalCoefficientRecipe recipe) {
			this.recipe = recipe;
		}

		@Override
		public void serializeRecipeData(JsonObject json) {
			json.addProperty("block_tag", recipe.blockTag.location().toString());
			json.addProperty("coefficient", recipe.coefficient);
		}

		@Override
		public ResourceLocation getId() {
			return recipe.getId();
		}

		@Override
		public RecipeSerializer<?> getType() {
			return RegistryManager.METAL_COEFFICIENT_SERIALIZER.get();
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
