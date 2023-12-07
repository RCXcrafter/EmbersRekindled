package com.rekindled.embers.item;

import com.rekindled.embers.api.EmbersAPI;

import net.minecraft.world.item.Item;

public class TinkerLensItem extends Item {

	public TinkerLensItem(Properties pProperties) {
		super(pProperties);
		EmbersAPI.registerLens(this);
	}
}
