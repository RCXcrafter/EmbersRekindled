package com.rekindled.embers.recipe;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.rekindled.embers.RegistryManager;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class StampingRecipe implements Recipe<StampingContext> {

	public static final Serializer SERIALIZER = new Serializer(); 

	public final ResourceLocation id;

	public final Ingredient stamp;
	public final Ingredient input;
	public final FluidIngredient fluid;

	public final ItemStack output;

	public StampingRecipe(ResourceLocation id, Ingredient stamp, Ingredient input, FluidIngredient fluid, ItemStack output) {
		this.id = id;
		this.stamp = stamp;
		this.input = input;
		this.fluid = fluid;
		this.output = output;
	}

	@Override
	public boolean matches(StampingContext context, Level pLevel) {
		for (int i = 0; i < context.getContainerSize(); i++) {
			if (input.test(context.getItem(i))) {
				if (stamp.test(context.stamp)) {
					for (FluidStack stack : fluid.getAllFluids()) {
						if (fluid.test(context.fluids.drain(stack, FluidAction.SIMULATE)))
							return true;
					}
				}
				return false;
			}
		}
		return false;
	}

	public ItemStack getOutput(RecipeWrapper context) {
		return output;
	}

	@Override
	public ItemStack assemble(StampingContext context) {
		for (int i = 0; i < context.getContainerSize(); i++) {
			if (input.test(context.getItem(i))) {
				context.removeItem(i, 1);
				break;
			}
		}
		for (FluidStack stack : fluid.getAllFluids()) {
			if (fluid.test(context.fluids.drain(stack, FluidAction.SIMULATE))) {
				context.fluids.drain(stack, FluidAction.EXECUTE);
				break;
			}
		}
		return output;
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(RegistryManager.STAMPER_ITEM.get());
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
		return RegistryManager.STAMPING.get();
	}

	@Override
	public ItemStack getResultItem() {
		return output;
	}

	@Override
	@Deprecated
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	public static class Serializer implements RecipeSerializer<StampingRecipe> {

		@Override
		public StampingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			Ingredient stamp = Ingredient.fromJson(json.get("stamp"));
			Ingredient input = Ingredient.EMPTY;
			FluidIngredient fluid = FluidIngredient.EMPTY;
			if (json.has("input"))
				input = Ingredient.fromJson(json.get("input"));
			if (json.has("fluid"))
				fluid = FluidIngredient.deserialize(json, "fluid");
			ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));

			return new StampingRecipe(recipeId, stamp, input, fluid, output);
		}

		@Override
		public @Nullable StampingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			Ingredient stamp = Ingredient.fromNetwork(buffer);
			Ingredient input = Ingredient.fromNetwork(buffer);
			FluidIngredient fluid = FluidIngredient.read(buffer);
			ItemStack output = buffer.readItem();

			return new StampingRecipe(recipeId, stamp, input, fluid, output);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, StampingRecipe recipe) {
			recipe.stamp.toNetwork(buffer);
			recipe.input.toNetwork(buffer);
			recipe.fluid.write(buffer);
			buffer.writeItemStack(recipe.output, false);
		}
	}
}
