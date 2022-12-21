package com.rekindled.embers.api.event;

import net.minecraft.world.level.block.entity.BlockEntity;

public class EmberEvent extends UpgradeEvent {
	EnumType type;
	double amount;

	public EmberEvent(BlockEntity tile, EnumType type, double amount) {
		super(tile);
		this.type = type;
		this.amount = amount;
	}

	public double getAmount() {
		return amount;
	}

	public EnumType getType() {
		return type;
	}

	public enum EnumType {
		PRODUCE,
		CONSUME
	}
}
