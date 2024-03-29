package com.rekindled.embers.compat.jei;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.recipe.IBoilingRecipe;

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

public class BoilingCategory implements IRecipeCategory<IBoilingRecipe> {

	private final IDrawable background;
	private final IDrawable icon;
	public static Component title = Component.translatable(Embers.MODID + ".jei.recipe.boiling");
	public static ResourceLocation texture = new ResourceLocation(Embers.MODID, "textures/gui/jei_boiler.png");

	public BoilingCategory(IGuiHelper helper) {
		background = helper.createDrawable(texture, 0, 0, 126, 28);
		icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(RegistryManager.MINI_BOILER_ITEM.get()));
	}

	@Override
	public RecipeType<IBoilingRecipe> getRecipeType() {
		return JEIPlugin.BOILING;
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
	public void setRecipe(IRecipeLayoutBuilder builder, IBoilingRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 26, 6)
		.addTooltipCallback(IngotTooltipCallback.INSTANCE)
		.setFluidRenderer(recipe.getDisplayInput().getFluids().get(0).getAmount(), false, 16, 16)
		.addIngredients(ForgeTypes.FLUID_STACK, recipe.getDisplayInput().getFluids());

		builder.addSlot(RecipeIngredientRole.OUTPUT, 84, 6)
		.addTooltipCallback(IngotTooltipCallback.INSTANCE)
		.setFluidRenderer(recipe.getDisplayOutput().getAmount(), false, 16, 16)
		.addIngredient(ForgeTypes.FLUID_STACK, recipe.getDisplayOutput());
	}
}
