package com.rekindled.embers.api.event;

import java.util.List;

import net.minecraft.world.level.block.entity.BlockEntity;

public class DialInformationEvent extends UpgradeEvent {
	List<String> information;
	String dialType;

	public DialInformationEvent(BlockEntity tile, List<String> information, String dialType) {
		super(tile);
		this.information = information;
		this.dialType = dialType;
	}

	public List<String> getInformation() {
		return information;
	}

	public String getDialType() {
		return dialType;
	}
}
