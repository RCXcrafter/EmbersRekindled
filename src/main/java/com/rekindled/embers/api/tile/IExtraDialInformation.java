package com.rekindled.embers.api.tile;

import net.minecraft.core.Direction;
import java.util.List;

public interface IExtraDialInformation {
	void addDialInformation(Direction facing, List<String> information, String dialType);

	default int getComparatorData(Direction facing, int data, String dialType) {
		return data;
	}
}
