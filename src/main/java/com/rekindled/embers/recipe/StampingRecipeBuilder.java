package com.rekindled.embers.recipe;

import java.util.function.Consumer;

import com.google.gson.JsonObject;
import com.rekindled.embers.RegistryManager;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class StampingRecipeBuilder {

	public ResourceLocation id;
	public Ingredient stamp;
	public Ingredient input = Ingredient.EMPTY;
	public FluidIngredient fluid = FluidIngredient.EMPTY;
	public ItemStack output;

	public static StampingRecipeBuilder create(ItemStack itemStack) {
		StampingRecipeBuilder builder = new StampingRecipeBuilder();
		builder.output = itemStack;
		builder.id = ForgeRegistries.ITEMS.getKey(itemStack.getItem());
		return builder;
	}

	public static StampingRecipeBuilder create(Item item) {
		return create(new ItemStack(item));
	}

	public StampingRecipeBuilder id(ResourceLocation id) {
		this.id = id;
		return this;
	}

	public StampingRecipeBuilder domain(String domain) {
		this.id = new ResourceLocation(domain, this.id.getPath());
		return this;
	}

	public StampingRecipeBuilder folder(String folder) {
		this.id = new ResourceLocation(id.getNamespace(), folder + "/" + id.getPath());
		return this;
	}

	public StampingRecipeBuilder stamp(Ingredient stamp) {
		this.stamp = stamp;
		return this;
	}

	public StampingRecipeBuilder stamp(ItemLike... stamp) {
		stamp(Ingredient.of(stamp));
		return this;
	}

	public StampingRecipeBuilder stamp(TagKey<Item> tag) {
		stamp(Ingredient.of(tag));
		return this;
	}

	public StampingRecipeBuilder input(Ingredient input) {
		this.input = input;
		return this;
	}

	public StampingRecipeBuilder input(ItemLike... input) {
		input(Ingredient.of(input));
		return this;
	}

	public StampingRecipeBuilder input(TagKey<Item> tag) {
		input(Ingredient.of(tag));
		return this;
	}

	public StampingRecipeBuilder fluid(FluidIngredient fluid) {
		this.fluid = fluid;
		return this;
	}

	public StampingRecipeBuilder fluid(Fluid fluid, int amount) {
		fluid(FluidIngredient.of(fluid, amount));
		return this;
	}

	public StampingRecipeBuilder fluid(FluidStack stack) {
		fluid(FluidIngredient.of(stack));
		return this;
	}

	public StampingRecipeBuilder fluid(TagKey<Fluid> fluid, int amount) {
		fluid(FluidIngredient.of(fluid, amount));
		return this;
	}

	public StampingRecipeBuilder fluid(FluidIngredient... ingredients) {
		fluid(FluidIngredient.of(ingredients));
		return this;
	}

	public StampingRecipeBuilder output(ItemStack output) {
		this.output = output;
		return this;
	}

	public StampingRecipeBuilder output(Item item) {
		output(new ItemStack(item));
		return this;
	}

	public StampingRecipe build() {
		return new StampingRecipe(id, stamp, input, fluid, output);
	}

	public void save(Consumer<FinishedRecipe> consumer) {
		consumer.accept(new Finished(build()));
	}

	public static class Finished implements FinishedRecipe {

		public final StampingRecipe recipe;

		public Finished(StampingRecipe recipe) {
			this.recipe = recipe;
		}

		@Override
		public void serializeRecipeData(JsonObject json) {
			JsonObject outputJson = new JsonObject();
			outputJson.addProperty("item", ForgeRegistries.ITEMS.getKey(recipe.output.getItem()).toString());
			int count = recipe.output.getCount();
			if (count > 1) {
				outputJson.addProperty("count", count);
			}
			json.add("output", outputJson);

			json.add("stamp", recipe.stamp.toJson());
			if (!recipe.input.isEmpty())
				json.add("input", recipe.input.toJson());
			if (recipe.fluid != FluidIngredient.EMPTY)
				json.add("fluid", recipe.fluid.serialize());

		}

		@Override
		public ResourceLocation getId() {
			return recipe.getId();
		}

		@Override
		public RecipeSerializer<?> getType() {
			return RegistryManager.STAMPING_SERIALIZER.get();
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
