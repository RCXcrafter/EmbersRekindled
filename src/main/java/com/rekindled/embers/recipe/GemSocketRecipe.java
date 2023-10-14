package com.rekindled.embers.recipe;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.item.IInflictorGem;
import com.rekindled.embers.api.item.IInflictorGemHolder;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class GemSocketRecipe implements CraftingRecipe {

	public static final Serializer SERIALIZER = new Serializer();

	public final ResourceLocation id;

	public final Ingredient ingredient;

	public GemSocketRecipe(ResourceLocation id, Ingredient ingredient) {
		this.id = id;
		this.ingredient = ingredient;
	}

	@Override
	public boolean matches(CraftingContainer container, Level level) {
		ItemStack cloak = ItemStack.EMPTY;
		int cloaks = 0;
		int strings = 0;
		int gems = 0;
		for (int i = 0; i < container.getContainerSize(); i ++) {
			ItemStack stack = container.getItem(i);
			if (stack.getItem() instanceof IInflictorGemHolder && ((IInflictorGemHolder) stack.getItem()).getAttachedGemCount(stack) == 0) {
				cloak = stack;
			}
		}
		for (int i = 0; i < container.getContainerSize(); i ++) {
			ItemStack stack = container.getItem(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof IInflictorGemHolder) {
					cloaks++;
				} else if (ingredient.test(stack)) {
					strings++;
				} else if (!cloak.isEmpty() && ((IInflictorGemHolder) cloak.getItem()).canAttachGem(cloak,stack)) {
					gems++;
				} else {
					return false;
				}
			}
		}
		return !cloak.isEmpty() && cloaks == 1 && strings == 1 && gems > 0 && gems <= ((IInflictorGemHolder)cloak.getItem()).getGemSlots(cloak);
	}

	@Override
	public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
		ItemStack capeStack = ItemStack.EMPTY;
		for (int i = 0; i < container.getContainerSize(); i ++) {
			if (!container.getItem(i).isEmpty() && container.getItem(i).getItem() instanceof IInflictorGemHolder) {
				capeStack = container.getItem(i).copy();
			}
		}
		if (!capeStack.isEmpty()) {
			int counter = 0;
			for (int i = 0; i < container.getContainerSize(); i ++) {
				ItemStack stack = container.getItem(i);
				if (!stack.isEmpty() && stack.getItem() instanceof IInflictorGem) {
					((IInflictorGemHolder)capeStack.getItem()).attachGem(capeStack, stack, counter);
					counter++;
				}
			}
			return capeStack;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 3;
	}

	@Override
	public ItemStack getResultItem(RegistryAccess registryAccess) {
		return new ItemStack(RegistryManager.ASHEN_CLOAK.get());
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public boolean showNotification() {
		return false;
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
	public CraftingBookCategory category() {
		return CraftingBookCategory.EQUIPMENT;
	}

	public static class Serializer implements RecipeSerializer<GemSocketRecipe> {

		@Override
		public GemSocketRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			Ingredient ingredient = Ingredient.fromJson(json.get("ingredient"));
			return new GemSocketRecipe(recipeId, ingredient);
		}

		@Override
		public @Nullable GemSocketRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			Ingredient ingredient = Ingredient.fromNetwork(buffer);
			return new GemSocketRecipe(recipeId, ingredient);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, GemSocketRecipe recipe) {
			recipe.ingredient.toNetwork(buffer);
		}
	}
}
