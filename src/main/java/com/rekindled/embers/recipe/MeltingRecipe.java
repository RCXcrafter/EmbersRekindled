package com.rekindled.embers.recipe;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.rekindled.embers.util.Misc;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

public class MeltingRecipe implements IMeltingRecipe {

	public static final Serializer SERIALIZER = new Serializer(); 

	public final ResourceLocation id;

	public final Ingredient ingredient;
	public final FluidStack output;
	public final FluidStack bonus;

	public MeltingRecipe(ResourceLocation id, Ingredient ingredient, FluidStack output, FluidStack bonus) {
		this.id = id;
		this.ingredient = ingredient;
		this.output = output;
		this.bonus = bonus;
	}

	public MeltingRecipe(ResourceLocation id, Ingredient ingredient, FluidStack output) {
		this(id, ingredient, output, FluidStack.EMPTY);
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
	public FluidStack getOutput(Container context) {
		return output;
	}

	@Override
	public FluidStack getBonus() {
		return bonus;
	}

	@Override
	public FluidStack process(Container context) {
		for (int i = 0; i < context.getContainerSize(); i++) {
			if (ingredient.test(context.getItem(i))) {
				context.removeItem(i, 1);
				break;
			}
		}
		return output;
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
	public FluidStack getDisplayOutput() {
		return output;
	}

	@Override
	public Ingredient getDisplayInput() {
		return ingredient;
	}

	public static class Serializer implements RecipeSerializer<MeltingRecipe> {

		@Override
		public MeltingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			Ingredient ingredient = Ingredient.fromJson(json.get("input"));
			FluidStack output = Misc.deserializeFluidStack(GsonHelper.getAsJsonObject(json, "output"));
			FluidStack bonus = FluidStack.EMPTY;
			if (json.has("bonus"))
				bonus = Misc.deserializeFluidStack(GsonHelper.getAsJsonObject(json, "bonus"));

			return new MeltingRecipe(recipeId, ingredient, output, bonus);
		}

		@Override
		public @Nullable MeltingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			Ingredient ingredient = Ingredient.fromNetwork(buffer);
			FluidStack output = FluidStack.readFromPacket(buffer);
			FluidStack bonus = FluidStack.readFromPacket(buffer);

			return new MeltingRecipe(recipeId, ingredient, output, bonus);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, MeltingRecipe recipe) {
			recipe.ingredient.toNetwork(buffer);
			recipe.output.writeToPacket(buffer);
			recipe.bonus.writeToPacket(buffer);
		}
	}
}
