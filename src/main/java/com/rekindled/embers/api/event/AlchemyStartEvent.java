package com.rekindled.embers.api.event;

import com.rekindled.embers.recipe.AlchemyContext;
import com.rekindled.embers.recipe.IAlchemyRecipe;

import net.minecraft.world.level.block.entity.BlockEntity;

public class AlchemyStartEvent extends UpgradeEvent {
	AlchemyContext context;
	IAlchemyRecipe recipe;

	public AlchemyStartEvent(BlockEntity tile, AlchemyContext context, IAlchemyRecipe recipe) {
		super(tile);
		this.context = context;
		this.recipe = recipe;
	}

	public AlchemyContext getContext() {
		return context;
	}

	//if this returns null, there is no recipe and alchemy does not actually start
	public IAlchemyRecipe getRecipe() {
		return recipe;
	}

	public void setRecipe(IAlchemyRecipe recipe) {
		this.recipe = recipe;
	}
}
