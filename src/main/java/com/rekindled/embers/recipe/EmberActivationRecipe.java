package com.rekindled.embers.recipe;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.rekindled.embers.RegistryManager;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class EmberActivationRecipe implements Recipe<Container> {

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

	public int getOutput(RecipeWrapper context) {
		return ember;
	}

	public int process(RecipeWrapper context) {
		for (int i = 0; i < context.getContainerSize(); i++) {
			if (ingredient.test(context.getItem(i))) {
				context.removeItem(i, 1);
				break;
			}
		}
		return ember;
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(RegistryManager.EMBER_ACTIVATOR_ITEM.get());
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
	public RecipeType<?> getType() {
		return RegistryManager.EMBER_ACTIVATION.get();
	}

	public Ingredient getDisplayInput() {
		return ingredient;
	}

	public int getDisplaOutput() {
		return ember;
	}

	@Override
	@Deprecated
	public ItemStack getResultItem() {
		return ItemStack.EMPTY;
	}

	@Override
	@Deprecated
	public ItemStack assemble(Container context) {
		return ItemStack.EMPTY;
	}

	@Override
	@Deprecated
	public boolean canCraftInDimensions(int width, int height) {
		return true;
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
