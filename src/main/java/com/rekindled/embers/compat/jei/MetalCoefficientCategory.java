package com.rekindled.embers.compat.jei;

import java.text.DecimalFormat;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.recipe.IMetalCoefficientRecipe;
import com.rekindled.embers.util.DecimalFormats;
import com.rekindled.embers.util.Misc;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class MetalCoefficientCategory implements IRecipeCategory<IMetalCoefficientRecipe> {

	private final IDrawable background;
	private final IDrawable icon;
	public static Component title = Component.translatable(Embers.MODID + ".jei.recipe.metal_coefficient");
	public static ResourceLocation texture = new ResourceLocation(Embers.MODID, "textures/gui/jei_item_text.png");

	public MetalCoefficientCategory(IGuiHelper helper) {
		background = helper.createDrawable(texture, 0, 0, 126, 28);
		icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(RegistryManager.PRESSURE_REFINERY_ITEM.get()));
	}

	@Override
	public RecipeType<IMetalCoefficientRecipe> getRecipeType() {
		return JEIPlugin.METAL_COEFFICIENT;
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
	public void setRecipe(IRecipeLayoutBuilder builder, IMetalCoefficientRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.CATALYST, 6, 6).addItemStacks(recipe.getDisplayInput());
	}

	@SuppressWarnings("resource")
	@Override
	public void draw(IMetalCoefficientRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
		DecimalFormat multiplierFormat = DecimalFormats.getDecimalFormat(Embers.MODID + ".decimal_format.ember_multiplier");
		Misc.drawComponents(Minecraft.getInstance().font, guiGraphics, 28, 10, Component.literal(multiplierFormat.format(recipe.getDisplayCoefficient())));
	}
}
