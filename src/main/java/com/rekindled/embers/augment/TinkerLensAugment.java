package com.rekindled.embers.augment;

import com.rekindled.embers.api.EmbersAPI;
import com.rekindled.embers.api.augment.AugmentUtil;

import net.minecraft.resources.ResourceLocation;

public class TinkerLensAugment extends AugmentBase {

	boolean inverted;

	public TinkerLensAugment(ResourceLocation name, boolean inverted) {
		super(name, 0.0);
		//MinecraftForge.EVENT_BUS.register(this);
		this.inverted = inverted;
		if (!inverted)
			EmbersAPI.registerLens((player) -> {
				return AugmentUtil.getArmorAugmentLevel(player, this) > 0;
			});
	}

	@Override
	public boolean countTowardsTotalLevel() {
		return false;
	}

	/*@SubscribeEvent
	public void shouldShowInfo(InfoGogglesEvent event) {
		Player player = event.getPlayer();
		int level = AugmentUtil.getArmorAugmentLevel(player, this);
		if (level > 0)
			event.setShouldDisplay(!inverted);
	}*/
}
