package com.rekindled.embers.augment;

import com.rekindled.embers.api.augment.AugmentUtil;
import com.rekindled.embers.api.event.InfoGogglesEvent;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TinkerLensAugment extends AugmentBase {

	boolean inverted;

	public TinkerLensAugment(ResourceLocation name, boolean inverted) {
		super(name, 0.0);
		MinecraftForge.EVENT_BUS.register(this);
		this.inverted = inverted;
	}

	@Override
	public boolean countTowardsTotalLevel() {
		return false;
	}

	@SubscribeEvent
	public void shouldShowInfo(InfoGogglesEvent event) {
		Player player = event.getPlayer();
		int level = AugmentUtil.getArmorAugmentLevel(player, this);
		if (level > 0)
			event.setShouldDisplay(!inverted);
	}
}
