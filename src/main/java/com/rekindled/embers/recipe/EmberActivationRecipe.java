package com.rekindled.embers.recipe;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class EmberActivationRecipe implements IEmberActivationRecipe {

	public static final Serializer SERIALIZER = new Serializer(); 

	public final ResourceLocation id;

	public final Ingredient ingredient;
	public final int ember;

	public EmberActivationRecipe(ResourceLocation id, Ingredient ingredient, int ember) {
		this.id = id;
		this.ingredient = ingredient;
		this.ember = ember;
	}

	@Override
	public boolean matches(Container context, Level pLevel) {
		for (int i = 0; i < context.getContainerSize(); i++) {
			if (ingredient.test(context.getItem(i)))
				return true;
		}
		return false;
	}

	@Override
	public int getOutput(Container context) {
		return ember;
	}

	@Override
	public int process(Container context) {
		for (int i = 0; i < context.getContainerSize(); i++) {
			if (ingredient.test(context.getItem(i))) {
				context.removeItem(i, 1);
				break;
			}
		}
		return ember;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public Ingredient getDisplayInput() {
		return ingredient;
	}

	@Override
	public int getDisplayOutput() {
		return ember;
	}

	public static class Serializer implements RecipeSerializer<EmberActivationRecipe> {

		@Override
		public EmberActivationRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			Ingredient ingredient = Ingredient.fromJson(json.get("input"));
			int ember = GsonHelper.getAsInt(json, "ember");

			return new EmberActivationRecipe(recipeId, ingredient, ember);
		}

		@Override
		public @Nullable EmberActivationRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			Ingredient ingredient = Ingredient.fromNetwork(buffer);
			int ember = buffer.readVarInt();

			return new EmberActivationRecipe(recipeId, ingredient, ember);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, EmberActivationRecipe recipe) {
			recipe.ingredient.toNetwork(buffer);
			buffer.writeVarInt(recipe.ember);
		}
	}
}
