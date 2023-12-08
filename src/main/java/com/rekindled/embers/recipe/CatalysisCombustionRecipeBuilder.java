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
import net.minecraftforge.registries.ForgeRegistries;

public class CatalysisCombustionRecipeBuilder {

	public ResourceLocation id;
	public Ingredient ingredient;
	public Ingredient machine;
	public int burnTime;
	public double multiplier;

	public static CatalysisCombustionRecipeBuilder create(Ingredient ingredient) {
		CatalysisCombustionRecipeBuilder builder = new CatalysisCombustionRecipeBuilder();
		builder.ingredient = ingredient;
		return builder;
	}

	public static CatalysisCombustionRecipeBuilder create(TagKey<Item> tag) {
		CatalysisCombustionRecipeBuilder builder = create(Ingredient.of(tag));
		builder.id = tag.location();
		return builder;
	}

	public static CatalysisCombustionRecipeBuilder create(ItemStack itemStack) {
		CatalysisCombustionRecipeBuilder builder = create(Ingredient.of(itemStack));
		builder.id = ForgeRegistries.ITEMS.getKey(itemStack.getItem());
		return builder;
	}

	public static CatalysisCombustionRecipeBuilder create(Item item) {
		return create(new ItemStack(item));
	}

	public CatalysisCombustionRecipeBuilder id(ResourceLocation id) {
		this.id = id;
		return this;
	}

	public CatalysisCombustionRecipeBuilder domain(String domain) {
		this.id = new ResourceLocation(domain, this.id.getPath());
		return this;
	}

	public CatalysisCombustionRecipeBuilder folder(String folder) {
		this.id = new ResourceLocation(id.getNamespace(), folder + "/" + id.getPath());
		return this;
	}

	public CatalysisCombustionRecipeBuilder catalysis() {
		this.machine = Ingredient.of(RegistryManager.CATALYSIS_CHAMBER_ITEM.get());
		return this;
	}

	public CatalysisCombustionRecipeBuilder combustion() {
		this.machine = Ingredient.of(RegistryManager.COMBUSTION_CHAMBER_ITEM.get());
		return this;
	}

	public CatalysisCombustionRecipeBuilder burnTime(int burnTime) {
		this.burnTime = burnTime;
		return this;
	}

	public CatalysisCombustionRecipeBuilder multiplier(double multiplier) {
		this.multiplier = multiplier;
		return this;
	}

	public CatalysisCombustionRecipe build() {
		return new CatalysisCombustionRecipe(id, ingredient, machine, burnTime, multiplier);
	}

	public void save(Consumer<FinishedRecipe> consumer) {
		consumer.accept(new Finished(build()));
	}

	public static class Finished implements FinishedRecipe {

		public final CatalysisCombustionRecipe recipe;

		public Finished(CatalysisCombustionRecipe recipe) {
			this.recipe = recipe;
		}

		@Override
		public void serializeRecipeData(JsonObject json) {
			json.add("input", recipe.ingredient.toJson());
			json.add("machine", recipe.machine.toJson());
			json.addProperty("burn_time", recipe.burnTime);
			json.addProperty("multiplier", recipe.multiplier);
		}

		@Override
		public ResourceLocation getId() {
			return recipe.getId();
		}

		@Override
		public RecipeSerializer<?> getType() {
			return RegistryManager.CATALYSIS_COMBUSTION_SERIALIZER.get();
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
