package com.rekindled.embers.compat.jei;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.recipe.BoringRecipe;
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
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class BoringCategory implements IRecipeCategory<BoringRecipe> {

	private final IDrawable background;
	private final IDrawable icon;
	public static Component title = Component.translatable(Embers.MODID + ".jei.recipe.boring");
	public static ResourceLocation texture = new ResourceLocation(Embers.MODID, "textures/gui/jei_boring.png");

	public BoringCategory(IGuiHelper helper) {
		background = helper.createDrawable(texture, 0, 0, 126, 98);
		icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(RegistryManager.EMBER_BORE_ITEM.get()));
	}

	@Override
	public RecipeType<BoringRecipe> getRecipeType() {
		return JEIPlugin.BORING;
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
	public void setRecipe(IRecipeLayoutBuilder builder, BoringRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.OUTPUT, 6, 6).addItemStack(recipe.getDisplayOutput().getStack());
		builder.addSlot(RecipeIngredientRole.CATALYST, 6, 26).addItemStacks(recipe.getDisplayInput());
	}

	public List<Component> getTooltipStrings(BoringRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
		int height = 48;
		List<Component> text = new ArrayList<Component>();

		if (recipe.minHeight != Integer.MIN_VALUE)
			height += 11;
		if (recipe.maxHeight != Integer.MAX_VALUE)
			height += 11;
		if (!recipe.dimensions.isEmpty()) {
			if (mouseY > height && mouseY < height + 11 && mouseX > 7 && mouseX < 119) {
				for (ResourceLocation dimension : recipe.dimensions) {
					text.add(Component.translatableWithFallback("dimension." + dimension.toLanguageKey(), WordUtils.capitalize(dimension.getPath().replace("_", " "))));
				}
			}
			height += 11;
		}
		if (!recipe.biomes.isEmpty()) {
			if (mouseY > height && mouseY < height + 11 && mouseX > 7 && mouseX < 119) {
				for (ResourceLocation biome : recipe.biomes) {
					text.add(Component.literal(biome.toString()));
				}
			}
			height += 11;
		}

		return text;
	}

	@SuppressWarnings("resource")
	@Override
	public void draw(BoringRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
		Font fontRenderer = Minecraft.getInstance().font;
		Misc.drawComponents(fontRenderer, guiGraphics, 28, 10, Component.translatable(Embers.MODID + ".jei.recipe.boring.weight", recipe.getDisplayOutput().getWeight()));
		Misc.drawComponents(fontRenderer, guiGraphics, 28, 30, Component.translatable(Embers.MODID + ".jei.recipe.boring.required_blocks"));
		List<Component> text = new ArrayList<Component>();
		if (recipe.minHeight != Integer.MIN_VALUE)
			text.add(Component.translatable(Embers.MODID + ".jei.recipe.boring.min_height", recipe.minHeight));
		if (recipe.maxHeight != Integer.MAX_VALUE)
			text.add(Component.translatable(Embers.MODID + ".jei.recipe.boring.max_height", recipe.maxHeight));
		if (!recipe.dimensions.isEmpty()) {
			text.add(Component.translatable(Embers.MODID + ".jei.recipe.boring.dimensions").withStyle(style -> style.withColor(0xFFB54D)));
			//for (ResourceLocation dimension : recipe.dimensions) {
			//text.add(Component.literal(dimension.toString()));
			//}
		}
		if (!recipe.biomes.isEmpty()) {
			text.add(Component.translatable(Embers.MODID + ".jei.recipe.boring.biomes").withStyle(style -> style.withColor(0xFFB54D)));
			//for (ResourceLocation biome : recipe.biomes) {
			//text.add(Component.translatable(biome.toString()));
			//}
		}
		Component[] components = new Component[text.size()];
		for (int i = 0; i < text.size(); i++) {
			components[i] = text.get(i);
		}
		Misc.drawComponents(fontRenderer, guiGraphics, 10, 48, components);
	}
}
