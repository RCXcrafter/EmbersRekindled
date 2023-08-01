package com.rekindled.embers.api.event;

import com.rekindled.embers.api.misc.AlchemyResult;
import com.rekindled.embers.recipe.AlchemyRecipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class AlchemyResultEvent extends UpgradeEvent {
	AlchemyRecipe recipe;
	AlchemyResult result;
	int consumeAmount;
	boolean isFailure;
	ItemStack resultStack;

	public AlchemyResultEvent(BlockEntity tile, AlchemyRecipe recipe, AlchemyResult result, int consumeAmount) {
		super(tile);
		this.recipe = recipe;
		this.result = result;
		this.consumeAmount = consumeAmount;
		this.isFailure = result.blackPins != recipe.inputs.size();
		this.resultStack = isFailure ? recipe.getfailureItem() : recipe.getResultItem();
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
		return resultStack;
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
}
