package com.rekindled.embers.upgrade;

import java.util.List;

import com.rekindled.embers.Embers;
import com.rekindled.embers.api.event.EmberEvent;
import com.rekindled.embers.api.event.UpgradeEvent;
import com.rekindled.embers.api.upgrades.UpgradeContext;
import com.rekindled.embers.particle.GlowParticleOptions;
import com.rekindled.embers.util.EmbersColors;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class HeatExchangerUpgrade extends DefaultUpgradeProvider {

	public static double multitplier = 0.9;
	public static double bonus = 300.0;

	public HeatExchangerUpgrade(BlockEntity tile) {
		super(new ResourceLocation(Embers.MODID, "heat_exchanger"), tile);
	}

	@Override
	public int getPriority() {
		return -90; //after the clockwork attenuator
	}

	@Override
	public double transformEmberProduction(BlockEntity tile, double ember, int distance, int count) {
		return ember * multitplier + bonus;
	}

	@Override
	public void throwEvent(BlockEntity tile, List<UpgradeContext> upgrades, UpgradeEvent event, int distance, int count) {
		if (event instanceof EmberEvent emberEvent && emberEvent.getType() == EmberEvent.EnumType.PRODUCE) {
			if (tile.getLevel() instanceof ServerLevel serverLevel) {
				serverLevel.sendParticles(new GlowParticleOptions(EmbersColors.EMBER_ID, new Vec3(0.0, 0.000001, 0.0), 2.0F, 40), this.tile.getBlockPos().getX() + 0.5, this.tile.getBlockPos().getY() + 0.5, this.tile.getBlockPos().getZ() + 0.5, (int) (0.04 * emberEvent.getAmount()), 0.12f, 0.12f, 0.12f, 0.0);
			}
		}
	}
}
