package com.rekindled.embers.compat.jei;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.recipe.IMeltingRecipe;

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
import net.minecraftforge.fluids.FluidType;

public class MeltingBonusCategory implements IRecipeCategory<IMeltingRecipe> {

	private final IDrawable background;
	private final IDrawable icon;
	public static Component title = Component.translatable(Embers.MODID + ".jei.recipe.geologic_separator");
	public static ResourceLocation texture = new ResourceLocation(Embers.MODID, "textures/gui/jei_geologic_separator.png");
	double scale = 1.0 / 32.0;

	public MeltingBonusCategory(IGuiHelper helper) {
		background = helper.createDrawable(texture, 0, 14, 108, 42);
		icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(RegistryManager.GEOLOGIC_SEPARATOR_ITEM.get()));
	}

	@Override
	public RecipeType<IMeltingRecipe> getRecipeType() {
		return JEIPlugin.MELTING_BONUS;
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
	public void setRecipe(IRecipeLayoutBuilder builder, IMeltingRecipe recipe, IFocusGroup focuses) {
		int capacity = FluidType.BUCKET_VOLUME * 4;
		int capacity2 = FluidType.BUCKET_VOLUME;

		builder.addSlot(RecipeIngredientRole.INPUT, 8, 14).addIngredients(recipe.getDisplayInput());

		builder.addSlot(RecipeIngredientRole.OUTPUT, 63, 6)
		.addTooltipCallback(IngotTooltipCallback.INSTANCE)
		.setFluidRenderer((int) (capacity * scale + recipe.getDisplayOutput().getAmount() * (1.0 - scale)), false, 16, 32)
		.addIngredient(ForgeTypes.FLUID_STACK, recipe.getDisplayOutput());

		builder.addSlot(RecipeIngredientRole.OUTPUT, 84, 26)
		.addTooltipCallback(IngotTooltipCallback.INSTANCE)
		.setFluidRenderer((int) (capacity2 * scale + recipe.getBonus().getAmount() * (1.0 - scale)), false, 16, 12)
		.addIngredient(ForgeTypes.FLUID_STACK, recipe.getBonus());
	}
}
