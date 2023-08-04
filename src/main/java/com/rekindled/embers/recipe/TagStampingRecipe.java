package com.rekindled.embers.recipe;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.rekindled.embers.util.Misc;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class TagStampingRecipe extends StampingRecipe {

	public static final Serializer SERIALIZER = new Serializer();

	public final TagKey<Item> tagOutput;

	public TagStampingRecipe(ResourceLocation id, Ingredient stamp, Ingredient input, FluidIngredient fluid, TagKey<Item> tagOutput) {
		super(id, stamp, input, fluid, ItemStack.EMPTY);
		this.tagOutput = tagOutput;
	}

	@Override
	public ItemStack getOutput(RecipeWrapper context) {
		return Misc.getTaggedItem(tagOutput);
	}
	
	public ItemStack getResultItem() {
		return Misc.getTaggedItem(tagOutput);
	}

	public static class Serializer implements RecipeSerializer<TagStampingRecipe> {

		@Override
		public TagStampingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			Ingredient stamp = Ingredient.fromJson(json.get("stamp"));
			Ingredient input = Ingredient.EMPTY;
			FluidIngredient fluid = FluidIngredient.EMPTY;
			if (json.has("input"))
				input = Ingredient.fromJson(json.get("input"));
			if (json.has("fluid"))
				fluid = FluidIngredient.deserialize(json, "fluid");
			TagKey<Item> output = ItemTags.create(new ResourceLocation(json.get("output").getAsString()));

			return new TagStampingRecipe(recipeId, stamp, input, fluid, output);
		}

		@Override
		public @Nullable TagStampingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			Ingredient stamp = Ingredient.fromNetwork(buffer);
			Ingredient input = Ingredient.fromNetwork(buffer);
			FluidIngredient fluid = FluidIngredient.read(buffer);
			TagKey<Item> output = ItemTags.create(buffer.readResourceLocation());

			return new TagStampingRecipe(recipeId, stamp, input, fluid, output);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, TagStampingRecipe recipe) {
			recipe.stamp.toNetwork(buffer);
			recipe.input.toNetwork(buffer);
			recipe.fluid.write(buffer);
			buffer.writeResourceLocation(recipe.tagOutput.location());
		}
	}
}
