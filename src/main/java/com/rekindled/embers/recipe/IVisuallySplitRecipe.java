package com.rekindled.embers.recipe;

import java.util.List;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;

public interface IVisuallySplitRecipe<R extends Recipe<Container>> {
	public List<R> getVisualRecipes();
}
