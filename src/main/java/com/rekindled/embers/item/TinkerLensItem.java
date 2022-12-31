package com.rekindled.embers.item;

import com.rekindled.embers.util.Misc;

import net.minecraft.world.item.Item;

public class TinkerLensItem extends Item {

	public TinkerLensItem(Properties pProperties) {
		super(pProperties);
		Misc.IS_WEARING_LENS.add((player) -> player.getMainHandItem().getItem() == TinkerLensItem.this || player.getOffhandItem().getItem() == TinkerLensItem.this);
	}
}
