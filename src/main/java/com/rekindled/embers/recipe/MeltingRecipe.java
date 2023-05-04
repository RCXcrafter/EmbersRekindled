package com.rekindled.embers.recipe;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.util.Misc;

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
import net.minecraftforge.fluids.FluidStack;

public class MeltingRecipe implements Recipe<Container> {

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

	public FluidStack getOutput(Container context) {
		return output;
	}

	public FluidStack getBonus() {
		return bonus;
	}

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
	public ItemStack getToastSymbol() {
		return new ItemStack(RegistryManager.MELTER_ITEM.get());
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
		return RegistryManager.MELTING.get();
	}

	public FluidStack getDisplayOutput() {
		return output;
	}

	public Ingredient getDisplayInput() {
		return ingredient;
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
