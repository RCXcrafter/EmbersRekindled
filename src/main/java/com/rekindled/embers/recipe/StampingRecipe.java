package com.rekindled.embers.recipe;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.util.Misc;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class StampingRecipe implements Recipe<StampingContext> {

	public static final Serializer SERIALIZER = new Serializer();

	public final ResourceLocation id;

	public final Ingredient stamp;
	public final Ingredient input;
	public final FluidIngredient fluid;

	public final Either<ItemStack, TagKey<Item>> output;

	public StampingRecipe(ResourceLocation id, Ingredient stamp, Ingredient input, FluidIngredient fluid, Either<ItemStack, TagKey<Item>> output) {
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
					if (fluid.test(context.fluids.getFluidInTank(0)))
						return true;
				}
				return false;
			}
		}
		return false;
	}

	@Override
	public ItemStack getResultItem(RegistryAccess registry) {
		return getOutput();
	}

	@Override
	public ItemStack assemble(StampingContext context, RegistryAccess registry) {
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
		return this.getOutput().copy();
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
	public boolean canCraftInDimensions(int pWidth, int pHeight) {
		return true;
	}

	public FluidIngredient getDisplayInputFluid() {
		return fluid;
	}

	public Ingredient getDisplayInput() {
		return input;
	}

	public Ingredient getDisplayStamp() {
		return stamp;
	}

	public ItemStack getOutput() {
		if (output.left().isPresent())
			return output.left().get();

		return Misc.getTaggedItem(output.right().get());
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

			JsonObject outputJson = json.get("output").getAsJsonObject();
			Either<ItemStack, TagKey<Item>> output;
			if (outputJson.has("item")) {
				output = Either.left(ShapedRecipe.itemStackFromJson(outputJson));
			} else {
				output = Either.right(ItemTags.create(new ResourceLocation(outputJson.get("tag").getAsString())));
			}

			return new StampingRecipe(recipeId, stamp, input, fluid, output);
		}

		@Override
		public @Nullable StampingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			Ingredient stamp = Ingredient.fromNetwork(buffer);
			Ingredient input = Ingredient.fromNetwork(buffer);
			FluidIngredient fluid = FluidIngredient.read(buffer);

			Either<ItemStack, TagKey<Item>> output;
			if (buffer.readBoolean()) {
				output = Either.left(buffer.readItem());
			} else {
				output = Either.right(ItemTags.create(buffer.readResourceLocation()));
			}

			return new StampingRecipe(recipeId, stamp, input, fluid, output);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, StampingRecipe recipe) {
			recipe.stamp.toNetwork(buffer);
			recipe.input.toNetwork(buffer);
			recipe.fluid.write(buffer);

			if (recipe.output.left().isPresent()) {
				buffer.writeBoolean(true);
				buffer.writeItem(recipe.output.left().get());
			} else {
				buffer.writeBoolean(false);
				buffer.writeResourceLocation(recipe.output.right().get().location());
			}
		}
	}
}
