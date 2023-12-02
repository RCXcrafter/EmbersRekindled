package com.rekindled.embers.api.misc;

import java.util.List;

import com.rekindled.embers.recipe.IAlchemyRecipe.PedestalContents;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

public class AlchemyResult {

	public List<PedestalContents> contents;
	public int blackPins;
	public int whitePins;

	public AlchemyResult(List<PedestalContents> contents, int blackPins, int whitePins) {
		this.contents = contents;
		this.blackPins = blackPins;
		this.whitePins = whitePins;
	}

	public ItemStack createResultStack(ItemStack stack) {
		CompoundTag nbt = new CompoundTag();
		nbt.putInt("blackPins", blackPins);
		nbt.putInt("whitePins", whitePins);

		ListTag aspectNBT = new ListTag();
		ListTag inputNBT = new ListTag();
		for (PedestalContents contents : contents) {
			aspectNBT.add(contents.aspect.serializeNBT());
			inputNBT.add(contents.input.serializeNBT());
		}
		nbt.put("aspects", aspectNBT);
		nbt.put("inputs", inputNBT);

		stack.setTag(nbt);
		return stack;
	}
}
