package com.rekindled.embers.compat.almostunified;

import com.almostreliable.unified.api.AlmostUnifiedLookup;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;

public class AlmostUnifiedAdapter {

	@Nullable
	public static Item getPreferredItemForTag(TagKey<Item> tag) {
		if (ModList.get().isLoaded("almostunified")) {
			return Adapter.getPreferredItemForTag(tag);
		}

		return null;
	}

	private static class Adapter {

		@Nullable
		public static Item getPreferredItemForTag(TagKey<Item> tag) {
			return AlmostUnifiedLookup.INSTANCE.getPreferredItemForTag(tag);
		}
	}

}
