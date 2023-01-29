package com.rekindled.embers.compat.jei;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.recipe.FluidIngredient;
import com.rekindled.embers.recipe.MixingRecipe;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;

public class MixingCategory implements IRecipeCategory<MixingRecipe> {

	private final IDrawable background;
	private final IDrawable icon;
	public static Component title = Component.translatable(Embers.MODID + ".jei.recipe.mixing");
	public static ResourceLocation texture = new ResourceLocation(Embers.MODID, "textures/gui/jei_mixer.png");

	public MixingCategory(IGuiHelper helper) {
		background = helper.createDrawable(texture, 0, 0, 108, 124);
		icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(RegistryManager.MIXER_CENTRIFUGE_ITEM.get()));
	}

	@Override
	public RecipeType<MixingRecipe> getRecipeType() {
		return JEIPlugin.MIXING;
	}

	@Override
	public Component getTitle() {
		return title;
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, MixingRecipe recipe, IFocusGroup focuses) {
		int count = 0;
		int capacity = 0;

		for (FluidIngredient ingredient : recipe.getDisplayInputFluids()) {
			for (FluidStack fluid : ingredient.getFluids()) {
				capacity = Math.max(fluid.getAmount(), capacity);
			}
		}
		capacity = Math.max(recipe.getDisplayOutput().getAmount(), capacity);
		capacity = Math.min(FluidType.BUCKET_VOLUME * 8, capacity + capacity / 4);

		for (FluidIngredient ingredient : recipe.getDisplayInputFluids()) {
			if (count > 2)
				break;
			if (count == 0) {
				builder.addSlot(RecipeIngredientRole.INPUT, 46, 7)
				.setFluidRenderer(capacity, false, 16, 32)
				.addIngredients(ForgeTypes.FLUID_STACK, ingredient.getFluids());
			}
			if (count == 1) {
				builder.addSlot(RecipeIngredientRole.INPUT, 8, 46)
				.setFluidRenderer(capacity, false, 16, 32)
				.addIngredients(ForgeTypes.FLUID_STACK, ingredient.getFluids());
			}
			if (count == 2) {
				builder.addSlot(RecipeIngredientRole.INPUT, 46, 84)
				.setFluidRenderer(capacity, false, 16, 32)
				.addIngredients(ForgeTypes.FLUID_STACK, ingredient.getFluids());
			}
			count++;
		}

		builder.addSlot(RecipeIngredientRole.OUTPUT, 84, 46)
		.setFluidRenderer(capacity, false, 16, 32)
		.addIngredient(ForgeTypes.FLUID_STACK, recipe.getDisplayOutput());
	}
}
