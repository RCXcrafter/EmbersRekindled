package com.rekindled.embers.recipe;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.rekindled.embers.api.augment.AugmentUtil;
import com.rekindled.embers.api.augment.IAugment;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class AnvilAugmentRemoveRecipe implements IDawnstoneAnvilRecipe, IVisuallySplitRecipe<IDawnstoneAnvilRecipe> {

	public static final Serializer SERIALIZER = new Serializer(); 

	public final ResourceLocation id;

	public AnvilAugmentRemoveRecipe(ResourceLocation id) {
		this.id = id;
	}

	@Override
	public boolean matches(Container context, Level pLevel) {
		if (context.getItem(1).isEmpty()) {
			for (IAugment augment : AugmentUtil.getAugments(context.getItem(0))) {
				if (augment.canRemove())
					return true;
			}
		}
		return false;
	}

	@Override
	public List<ItemStack> getOutput(Container context) {
		ItemStack tool = context.getItem(0).copy();
		List<ItemStack> outputs = AugmentUtil.removeAllAugments(tool);
		outputs.add(tool);
		return outputs;
	}

	@Override
	public List<IDawnstoneAnvilRecipe> getVisualRecipes() {
		return List.of();
	}

	@Override
	public List<ItemStack> getDisplayInputBottom() {
		return List.of();
	}

	@Override
	public List<ItemStack> getDisplayInputTop() {
		return List.of();
	}

	@Override
	public List<ItemStack> getDisplayOutput() {
		return List.of();
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	public static class Serializer implements RecipeSerializer<AnvilAugmentRemoveRecipe> {

		@Override
		public AnvilAugmentRemoveRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			return new AnvilAugmentRemoveRecipe(recipeId);
		}

		@Override
		public @Nullable AnvilAugmentRemoveRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			return new AnvilAugmentRemoveRecipe(recipeId);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, AnvilAugmentRemoveRecipe recipe) {
		}
	}
}
