package com.rekindled.embers.recipe;

import java.util.HashSet;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.util.WeightedItemStack;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

public class BoringRecipe implements Recipe<BoringContext> {

	public static final Serializer SERIALIZER = new Serializer(); 

	public final ResourceLocation id;

	public final WeightedItemStack result;
	public final int minHeight;
	public final int maxHeight;
	public final HashSet<ResourceLocation> dimensions;
	public final HashSet<ResourceLocation> biomes;

	public BoringRecipe(ResourceLocation id, WeightedItemStack result, int minHeight, int maxHeight, HashSet<ResourceLocation> dimensions, HashSet<ResourceLocation> biomes) {
		this.id = id;
		this.result = result;
		this.maxHeight = maxHeight;
		this.minHeight = minHeight;
		this.dimensions = dimensions;
		this.biomes = biomes;
	}

	@Override
	public boolean matches(BoringContext context, Level pLevel) {
		if (!dimensions.isEmpty() && !dimensions.contains(context.dimension))
			return false;
		if (!biomes.isEmpty() && !biomes.contains(context.biome))
			return false;
		return context.height >= minHeight && context.height <= maxHeight;
	}

	public WeightedItemStack getOutput(BoringContext context) {
		return result;
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(RegistryManager.EMBER_BORE_ITEM.get());
	}

	@Override
	public ItemStack getResultItem() {
		return result.getStack();
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
		return RegistryManager.BORING.get();
	}

	public WeightedItemStack getDisplayOutput() {
		return result;
	}

	@Override
	@Deprecated
	public ItemStack assemble(BoringContext context) {
		return ItemStack.EMPTY;
	}

	@Override
	@Deprecated
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	public static class Serializer implements RecipeSerializer<BoringRecipe> {

		@Override
		public BoringRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			ItemStack stack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));
			int weight = GsonHelper.getAsInt(json, "weight");

			int minHeight = GsonHelper.getAsInt(json, "min_height", Integer.MIN_VALUE);
			int maxHeight = GsonHelper.getAsInt(json, "max_height", Integer.MAX_VALUE);
			HashSet<ResourceLocation> dimensions = new HashSet<>();
			HashSet<ResourceLocation> biomes = new HashSet<>();

			JsonArray dimJson = GsonHelper.getAsJsonArray(json, "dimensions", null);
			if (dimJson != null) {
				for (JsonElement element : dimJson) {
					dimensions.add(new ResourceLocation(element.getAsString()));
				}
			}
			JsonArray biomeJson = GsonHelper.getAsJsonArray(json, "biomes", null);
			if (biomeJson != null) {
				for (JsonElement element : biomeJson) {
					biomes.add(new ResourceLocation(element.getAsString()));
				}
			}

			return new BoringRecipe(recipeId, new WeightedItemStack(stack, weight), minHeight, maxHeight, dimensions, biomes);
		}

		@Override
		public @Nullable BoringRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			ItemStack stack = buffer.readItem();
			int weight = buffer.readVarInt();

			int minHeight = buffer.readVarInt();
			int maxHeight = buffer.readVarInt();
			HashSet<ResourceLocation> dimensions = buffer.readCollection((i) -> new HashSet<>(), FriendlyByteBuf::readResourceLocation);
			HashSet<ResourceLocation> biomes = buffer.readCollection((i) -> new HashSet<>(), FriendlyByteBuf::readResourceLocation);

			return new BoringRecipe(recipeId, new WeightedItemStack(stack, weight), minHeight, maxHeight, dimensions, biomes);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, BoringRecipe recipe) {
			buffer.writeItemStack(recipe.result.getStack(), false);
			buffer.writeVarInt(recipe.result.getWeight().asInt());
			buffer.writeVarInt(recipe.minHeight);
			buffer.writeVarInt(recipe.maxHeight);
			buffer.writeCollection(recipe.dimensions, FriendlyByteBuf::writeResourceLocation);
			buffer.writeCollection(recipe.biomes, FriendlyByteBuf::writeResourceLocation);
		}
	}
}
