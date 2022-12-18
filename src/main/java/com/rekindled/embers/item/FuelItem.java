package com.rekindled.embers.item;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

public class FuelItem extends Item {

	public final int burnTime;

	public FuelItem(Properties pProperties, int burnTime) {
		super(pProperties);
		this.burnTime = burnTime;
	}

	@Override
	public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
		return burnTime;
	}
}
