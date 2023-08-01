package com.rekindled.embers.compat.jei;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.recipe.AlchemyRecipe;

import mezz.jei.api.constants.VanillaTypes;
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
import net.minecraft.world.phys.Vec3;

public class AlchemyCategory implements IRecipeCategory<AlchemyRecipe> {

	private final IDrawable background;
	private final IDrawable icon;
	public static Component title = Component.translatable(Embers.MODID + ".jei.recipe.alchemy");
	public static ResourceLocation texture = new ResourceLocation(Embers.MODID, "textures/gui/jei_alchemy.png");

	public AlchemyCategory(IGuiHelper helper) {
		background = helper.createDrawable(texture, 0, 0, 126, 98);
		icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(RegistryManager.ALCHEMY_TABLET_ITEM.get()));
	}

	@Override
	public RecipeType<AlchemyRecipe> getRecipeType() {
		return JEIPlugin.ALCHEMY;
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
	public void setRecipe(IRecipeLayoutBuilder builder, AlchemyRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 32, 32).addIngredients(recipe.tablet);
		builder.addSlot(RecipeIngredientRole.OUTPUT, 101, 32).addItemStack(recipe.getResultItem());
		Vec3 center = new Vec3(0, 25, 0);
		for (int i = 0; i < recipe.inputs.size(); i++) {
			Vec3 rotated = center.zRot((float) (i * 2.0 * Math.PI / recipe.inputs.size()));
			builder.addSlot(RecipeIngredientRole.INPUT, (int) (32 + rotated.x()), (int) (32 + rotated.y())).addIngredients(recipe.inputs.get(i));
		}
		for (int i = 0; i < recipe.aspects.size(); i++) {
			builder.addSlot(RecipeIngredientRole.CATALYST, 63 - 8 * recipe.aspects.size() + 16 * i, 80).addIngredients(recipe.aspects.get(i));
		}
	}
}
