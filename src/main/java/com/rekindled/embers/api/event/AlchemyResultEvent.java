package com.rekindled.embers.api.event;

import com.rekindled.embers.api.misc.AlchemyResult;
import com.rekindled.embers.recipe.IAlchemyRecipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class AlchemyResultEvent extends UpgradeEvent {
	IAlchemyRecipe recipe;
	AlchemyResult result;
	int consumeAmount;
	boolean isFailure;
	ItemStack resultStack = null;

	public AlchemyResultEvent(BlockEntity tile, IAlchemyRecipe recipe, AlchemyResult result, int consumeAmount) {
		super(tile);
		this.setRecipe(recipe);
		this.result = result;
		this.consumeAmount = consumeAmount;
		this.isFailure = result.blackPins != recipe.getInputs().size();
	}

	public int getConsumeAmount() {
		return consumeAmount;
	}

	public void setconsumeAmount(int consumeAmount) {
		this.consumeAmount = consumeAmount;
	}

	public AlchemyResult getResult() {
		return result;
	}

	public void setResult(AlchemyResult result) {
		this.result = result;
	}

	public ItemStack getResultStack() {
		if (resultStack != null)
			return resultStack;
		return isFailure ? recipe.getfailureItem() : recipe.getResultItem();
	}

	public void setResultStack(ItemStack resultStack) {
		this.resultStack = resultStack;
	}

	public boolean isFailure() {
		return isFailure;
	}

	public void setFailure(boolean isFailure) {
		this.isFailure = isFailure;
	}

	public IAlchemyRecipe getRecipe() {
		return recipe;
	}

	public void setRecipe(IAlchemyRecipe recipe) {
		this.recipe = recipe;
	}
}
