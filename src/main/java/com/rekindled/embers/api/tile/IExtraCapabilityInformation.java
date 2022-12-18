package com.rekindled.embers.api.tile;

import java.util.List;

import com.rekindled.embers.Embers;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;

public interface IExtraCapabilityInformation {
	default boolean hasCapabilityDescription(Capability<?> capability) {
		return false;
	}

	default void addCapabilityDescription(List<String> strings, Capability<?> capability, Direction facing) {
		//NOOP
	}

	default void addOtherDescription(List<String> strings, Direction facing) {
		//NOOP
	}

	static String formatCapability(EnumIOType ioType, String type, String filter) {
		String typeString = filter == null ? I18n.get(type) : I18n.get(Embers.MODID + ".tooltip.goggles.filter", I18n.get(type), filter);
		switch (ioType) {
		case NONE:
			return null;
		case INPUT:
			return I18n.get(Embers.MODID + ".tooltip.goggles.input", typeString);
		case OUTPUT:
			return I18n.get(Embers.MODID + ".tooltip.goggles.output", typeString);
		default:
			return I18n.get(Embers.MODID + ".tooltip.goggles.storage", typeString);
		}
	}

	enum EnumIOType {
		NONE,
		INPUT,
		OUTPUT,
		BOTH
	}
}
