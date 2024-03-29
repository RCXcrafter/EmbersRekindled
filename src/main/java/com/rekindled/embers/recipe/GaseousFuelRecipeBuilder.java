package com.rekindled.embers.recipe;

import java.util.function.Consumer;

import com.google.gson.JsonObject;
import com.rekindled.embers.RegistryManager;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class GaseousFuelRecipeBuilder {

	public ResourceLocation id;
	public FluidIngredient input;
	public int burnTime;
	public double powerMultiplier;

	public static GaseousFuelRecipeBuilder create(ResourceLocation id) {
		GaseousFuelRecipeBuilder builder = new GaseousFuelRecipeBuilder();
		builder.id = id;
		return builder;
	}

	public static GaseousFuelRecipeBuilder create(FluidStack fluidStack) {
		GaseousFuelRecipeBuilder builder = create(ForgeRegistries.FLUIDS.getKey(fluidStack.getFluid()));
		builder.input = FluidIngredient.of(fluidStack);
		return builder;
	}

	public static GaseousFuelRecipeBuilder create(Fluid fluid, int amount) {
		return create(new FluidStack(fluid, amount));
	}

	public GaseousFuelRecipeBuilder id(ResourceLocation id) {
		this.id = id;
		return this;
	}

	public GaseousFuelRecipeBuilder domain(String domain) {
		this.id = new ResourceLocation(domain, this.id.getPath());
		return this;
	}

	public GaseousFuelRecipeBuilder folder(String folder) {
		this.id = new ResourceLocation(id.getNamespace(), folder + "/" + id.getPath());
		return this;
	}

	public GaseousFuelRecipeBuilder input(FluidIngredient fluid) {
		this.input = fluid;
		return this;
	}

	public GaseousFuelRecipeBuilder input(Fluid fluid, int amount) {
		input(FluidIngredient.of(fluid, amount));
		return this;
	}

	public GaseousFuelRecipeBuilder input(FluidStack stack) {
		input(FluidIngredient.of(stack));
		return this;
	}

	public GaseousFuelRecipeBuilder input(TagKey<Fluid> fluid, int amount) {
		input(FluidIngredient.of(fluid, amount));
		return this;
	}

	public GaseousFuelRecipeBuilder input(FluidIngredient... ingredients) {
		input(FluidIngredient.of(ingredients));
		return this;
	}

	public GaseousFuelRecipeBuilder burnTime(int burnTime) {
		this.burnTime = burnTime;
		return this;
	}

	public GaseousFuelRecipeBuilder powerMultiplier(double powerMultiplier) {
		this.powerMultiplier = powerMultiplier;
		return this;
	}

	public GaseousFuelRecipe build() {
		return new GaseousFuelRecipe(id, input, burnTime, powerMultiplier);
	}

	public void save(Consumer<FinishedRecipe> consumer) {
		consumer.accept(new Finished(build()));
	}

	public static class Finished implements FinishedRecipe {

		public final GaseousFuelRecipe recipe;

		public Finished(GaseousFuelRecipe recipe) {
			this.recipe = recipe;
		}

		@Override
		public void serializeRecipeData(JsonObject json) {
			json.add("input", recipe.input.serialize());

			json.addProperty("burn_time", recipe.burnTime);
			json.addProperty("power_multiplier", recipe.powerMultiplier);
		}

		@Override
		public ResourceLocation getId() {
			return recipe.getId();
		}

		@Override
		public RecipeSerializer<?> getType() {
			return RegistryManager.GASEOUS_FUEL_SERIALIZER.get();
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
