package com.rekindled.embers.recipe;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.rekindled.embers.RegistryManager;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class GaseousFuelRecipe implements Recipe<FluidHandlerContext> {

	public static final Serializer SERIALIZER = new Serializer(); 

	public final ResourceLocation id;

	public final FluidIngredient input;
	public final int burnTime;
	public final double powerMultiplier;

	public GaseousFuelRecipe(ResourceLocation id, FluidIngredient input, int burnTime, double powerMultiplier) {
		this.id = id;
		this.input = input;
		this.burnTime = burnTime;
		this.powerMultiplier = powerMultiplier;
	}

	@Override
	public boolean matches(FluidHandlerContext context, Level pLevel) {
		for (FluidStack stack : input.getAllFluids()) {
			if (input.test(context.fluid.drain(stack, FluidAction.SIMULATE))) {
				return true;
			}
		}
		return false;
	}

	public int getBurnTime(FluidHandlerContext context) {
		return burnTime;
	}

	public double getPowerMultiplier(FluidHandlerContext context) {
		return powerMultiplier;
	}

	public int process(FluidHandlerContext context, int amount) {
		int trueAmount = amount;
		for (FluidStack stack : input.getAllFluids()) {
			FluidStack drainStack = new FluidStack(stack, stack.getAmount() * amount);
			if (input.test(context.fluid.drain(drainStack, FluidAction.SIMULATE))) {
				trueAmount = context.fluid.drain(drainStack, FluidAction.EXECUTE).getAmount() / stack.getAmount();
				break;
			}
		}
		return burnTime * trueAmount;
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(RegistryManager.WILDFIRE_STIRLING_ITEM.get());
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
		return RegistryManager.GASEOUS_FUEL.get();
	}

	public FluidIngredient getDisplayInput() {
		return input;
	}

	@Override
	@Deprecated
	public ItemStack assemble(FluidHandlerContext context, RegistryAccess registry) {
		return ItemStack.EMPTY;
	}

	@Override
	@Deprecated
	public ItemStack getResultItem(RegistryAccess registry) {
		return ItemStack.EMPTY;
	}

	@Override
	@Deprecated
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	public static class Serializer implements RecipeSerializer<GaseousFuelRecipe> {

		@Override
		public GaseousFuelRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			FluidIngredient input = FluidIngredient.deserialize(json, "input");
			int burnTime = GsonHelper.getAsInt(json, "burn_time");
			double powerMultiplier = GsonHelper.getAsDouble(json, "power_multiplier");

			return new GaseousFuelRecipe(recipeId, input, burnTime, powerMultiplier);
		}

		@Override
		public @Nullable GaseousFuelRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			FluidIngredient input = FluidIngredient.read(buffer);
			int burnTime = buffer.readInt();
			double powerMultiplier = buffer.readDouble();

			return new GaseousFuelRecipe(recipeId, input, burnTime, powerMultiplier);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, GaseousFuelRecipe recipe) {
			recipe.input.write(buffer);
			buffer.writeInt(recipe.burnTime);
			buffer.writeDouble(recipe.powerMultiplier);
		}
	}
}
