package com.rekindled.embers.recipe;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.rekindled.embers.RegistryManager;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class CatalysisCombustionRecipe implements Recipe<CatalysisCombustionContext> {

	public static final Serializer SERIALIZER = new Serializer(); 

	public final ResourceLocation id;

	public final Ingredient ingredient;
	public final Ingredient machine;
	public final int burnTime;
	public final double multiplier;

	public CatalysisCombustionRecipe(ResourceLocation id, Ingredient ingredient, Ingredient machine, int burnTime, double multiplier) {
		this.id = id;
		this.ingredient = ingredient;
		this.machine = machine;
		this.burnTime = burnTime;
		this.multiplier = multiplier;
	}

	@Override
	public boolean matches(CatalysisCombustionContext context, Level pLevel) {
		if (machine.test(context.machine)) {
			for (int i = 0; i < context.getContainerSize(); i++) {
				if (ingredient.test(context.getItem(i)))
					return true;
			}
		}
		return false;
	}

	public int getBurnTIme(CatalysisCombustionContext context) {
		return burnTime;
	}

	public double getmultiplier(CatalysisCombustionContext context) {
		return multiplier;
	}

	public int process(CatalysisCombustionContext context) {
		for (int i = 0; i < context.getContainerSize(); i++) {
			if (ingredient.test(context.getItem(i))) {
				context.removeItem(i, 1);
				break;
			}
		}
		return burnTime;
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(RegistryManager.IGNEM_REACTOR_ITEM.get());
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
		return RegistryManager.CATALYSIS_COMBUSTION.get();
	}

	public Ingredient getDisplayInput() {
		return ingredient;
	}

	public Ingredient getDisplayMachine() {
		return machine;
	}

	public int getDisplayTime() {
		return burnTime;
	}

	public double getDisplayMultiplier() {
		return multiplier;
	}

	@Override
	@Deprecated
	public ItemStack getResultItem(RegistryAccess registry) {
		return ItemStack.EMPTY;
	}

	@Override
	@Deprecated
	public ItemStack assemble(CatalysisCombustionContext context, RegistryAccess registry) {
		return ItemStack.EMPTY;
	}

	@Override
	@Deprecated
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	public static class Serializer implements RecipeSerializer<CatalysisCombustionRecipe> {

		@Override
		public CatalysisCombustionRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			Ingredient ingredient = Ingredient.fromJson(json.get("input"));
			Ingredient machine = Ingredient.fromJson(json.get("machine"));
			int burnTime = GsonHelper.getAsInt(json, "burn_time");
			double multiplier = GsonHelper.getAsDouble(json, "multiplier");

			return new CatalysisCombustionRecipe(recipeId, ingredient, machine, burnTime, multiplier);
		}

		@Override
		public @Nullable CatalysisCombustionRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			Ingredient ingredient = Ingredient.fromNetwork(buffer);
			Ingredient machine = Ingredient.fromNetwork(buffer);
			int burnTime = buffer.readVarInt();
			double multiplier = buffer.readDouble();

			return new CatalysisCombustionRecipe(recipeId, ingredient, machine, burnTime, multiplier);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, CatalysisCombustionRecipe recipe) {
			recipe.ingredient.toNetwork(buffer);
			recipe.machine.toNetwork(buffer);
			buffer.writeVarInt(recipe.burnTime);
			buffer.writeDouble(recipe.multiplier);
		}
	}
}
