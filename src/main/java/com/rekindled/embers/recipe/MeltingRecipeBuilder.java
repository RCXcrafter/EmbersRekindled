package com.rekindled.embers.recipe;

import java.util.function.Consumer;

import com.google.gson.JsonObject;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.util.FluidOutput;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class MeltingRecipeBuilder {

	public ResourceLocation id;
	public Ingredient ingredient;
	public FluidOutput output;
	public FluidOutput bonus = FluidOutput.EMPTY;

	public static MeltingRecipeBuilder create(Ingredient ingredient) {
		MeltingRecipeBuilder builder = new MeltingRecipeBuilder();
		builder.ingredient = ingredient;
		return builder;
	}

	public static MeltingRecipeBuilder create(TagKey<Item> tag) {
		MeltingRecipeBuilder builder = create(Ingredient.of(tag));
		builder.id = tag.location();
		return builder;
	}

	public static MeltingRecipeBuilder create(ItemStack itemStack) {
		MeltingRecipeBuilder builder = create(Ingredient.of(itemStack));
		builder.id = ForgeRegistries.ITEMS.getKey(itemStack.getItem());
		return builder;
	}

	public static MeltingRecipeBuilder create(Item item) {
		return create(new ItemStack(item));
	}

	public MeltingRecipeBuilder id(ResourceLocation id) {
		this.id = id;
		return this;
	}

	public MeltingRecipeBuilder domain(String domain) {
		this.id = new ResourceLocation(domain, this.id.getPath());
		return this;
	}

	public MeltingRecipeBuilder folder(String folder) {
		this.id = new ResourceLocation(id.getNamespace(), folder + "/" + id.getPath());
		return this;
	}

	public MeltingRecipeBuilder bonusName(String stuff) {
		this.id = new ResourceLocation(id.getNamespace(), id.getPath() + "_" + stuff);
		return this;
	}

	public MeltingRecipeBuilder output(FluidStack output) {
		this.output = new FluidOutput(output);
		return this;
	}

	public MeltingRecipeBuilder output(Fluid fluid, int amount) {
		output(new FluidStack(fluid, amount));
		return this;
	}

	public MeltingRecipeBuilder output(TagKey<Fluid> tag, int amount) {
		this.output = new FluidOutput(tag, amount);
		return this;
	}

	public MeltingRecipeBuilder bonus(FluidStack bonus) {
		this.bonus = new FluidOutput(bonus);
		return this;
	}

	public MeltingRecipeBuilder bonus(Fluid fluid, int amount) {
		bonus(new FluidStack(fluid, amount));
		return this;
	}

	public MeltingRecipeBuilder bonus(TagKey<Fluid> tag, int amount) {
		this.bonus = new FluidOutput(tag, amount);
		return this;
	}

	public MeltingRecipe build() {
		return new MeltingRecipe(id, ingredient, output, bonus);
	}

	public void save(Consumer<FinishedRecipe> consumer) {
		consumer.accept(new Finished(build()));
	}

	public static class Finished implements FinishedRecipe {

		public final MeltingRecipe recipe;

		public Finished(MeltingRecipe recipe) {
			this.recipe = recipe;
		}

		@Override
		public void serializeRecipeData(JsonObject json) {
			json.add("input", recipe.ingredient.toJson());
			json.add("output", recipe.output.toJson());
			if (!recipe.bonus.isEmpty())
				json.add("bonus", recipe.bonus.toJson());
		}

		@Override
		public ResourceLocation getId() {
			return recipe.getId();
		}

		@Override
		public RecipeSerializer<?> getType() {
			return RegistryManager.MELTING_SERIALIZER.get();
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
