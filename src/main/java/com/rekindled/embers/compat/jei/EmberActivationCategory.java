package com.rekindled.embers.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.recipe.EmberActivationRecipe;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class EmberActivationCategory implements IRecipeCategory<EmberActivationRecipe> {

	private final IDrawable background;
	private final IDrawable icon;
	public static Component title = Component.translatable(Embers.MODID + ".jei.recipe.ember_activation");
	public static ResourceLocation texture = new ResourceLocation(Embers.MODID, "textures/gui/jei_ember_activation.png");

	public EmberActivationCategory(IGuiHelper helper) {
		background = helper.createDrawable(texture, 0, 0, 126, 28);
		icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(RegistryManager.EMBER_ACTIVATOR_ITEM.get()));
	}

	@Override
	public RecipeType<EmberActivationRecipe> getRecipeType() {
		return JEIPlugin.EMBER_ACTIVATION;
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
	public void setRecipe(IRecipeLayoutBuilder builder, EmberActivationRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 6, 6).addIngredients(recipe.getDisplayInput());
	}

	@SuppressWarnings("resource")
	@Override
	public void draw(EmberActivationRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) { 
		Misc.drawComponents(Minecraft.getInstance().font, stack, 28, 10, Component.translatable(Embers.MODID + ".jei.recipe.ember_activation.ember", recipe.getDisplaOutput()));
	}
}
