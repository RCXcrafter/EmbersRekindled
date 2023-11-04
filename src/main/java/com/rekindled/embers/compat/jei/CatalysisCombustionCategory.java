package com.rekindled.embers.compat.jei;

import java.text.DecimalFormat;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.recipe.CatalysisCombustionRecipe;
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

public class CatalysisCombustionCategory implements IRecipeCategory<CatalysisCombustionRecipe> {

	private final IDrawable background;
	private final IDrawable icon;
	public static Component title = Component.translatable(Embers.MODID + ".jei.recipe.catalysis_combustion");
	public static ResourceLocation texture = new ResourceLocation(Embers.MODID, "textures/gui/jei_catalysis_combustion.png");

	public CatalysisCombustionCategory(IGuiHelper helper) {
		background = helper.createDrawable(texture, 0, 0, 126, 46);
		icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(RegistryManager.IGNEM_REACTOR_ITEM.get()));
	}

	@Override
	public RecipeType<CatalysisCombustionRecipe> getRecipeType() {
		return JEIPlugin.CATALYSIS_COMBUSTION;
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
	public void setRecipe(IRecipeLayoutBuilder builder, CatalysisCombustionRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.CATALYST, 6, 6).addIngredients(recipe.getDisplayMachine());
		builder.addSlot(RecipeIngredientRole.INPUT, 6, 24).addIngredients(recipe.getDisplayInput());
	}

	@SuppressWarnings("resource")
	@Override
	public void draw(CatalysisCombustionRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
		DecimalFormat multiplierFormat = DecimalFormats.getDecimalFormat(Embers.MODID + ".decimal_format.ember_multiplier");
		Misc.drawComponents(Minecraft.getInstance().font, guiGraphics, 28, 10, Component.translatable(Embers.MODID + ".jei.recipe.catalysis_combustion.multiplier", multiplierFormat.format(recipe.getDisplayMultiplier())));
		Misc.drawComponents(Minecraft.getInstance().font, guiGraphics, 28, 28, Component.translatable(Embers.MODID + ".jei.recipe.catalysis_combustion.burn_time", recipe.getDisplayTime()));
	}
}
