package com.rekindled.embers.augment;

import com.rekindled.embers.api.augment.AugmentUtil;
import com.rekindled.embers.particle.GlowParticleOptions;
import com.rekindled.embers.util.EmberInventoryUtil;
import com.rekindled.embers.util.Misc;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SuperheaterAugment extends AugmentBase {

	public SuperheaterAugment(ResourceLocation id) {
		super(id, 2.0);
		MinecraftForge.EVENT_BUS.register(this);
	}

	//for technical reasons, the autosmelt part is in a separate class
	//SuperHeaterLootModifier

	private double getBurnBonus(double resonance) {
		if(resonance > 1)
			return 1 + (resonance - 1) * 0.5;
		else
			return resonance;
	}

	private double getDamageBonus(double resonance) {
		if(resonance > 1)
			return 1 + (resonance - 1) * 1.0;
		else
			return resonance;
	}

	@SubscribeEvent
	public void onHit(LivingHurtEvent event) {
		if (event.getSource().getEntity() instanceof Player player) {
			ItemStack s = player.getMainHandItem();
			if (AugmentUtil.hasHeat(s)) {
				int level = AugmentUtil.getAugmentLevel(s, this);
				if (level > 0 && EmberInventoryUtil.getEmberTotal(player) >= cost) {
					double resonance = Misc.getEmberResonance(s);
					int burnTime = (int) (Math.pow(2, level - 1) * 5 * getBurnBonus(resonance));
					float extraDamage = (float) (level * getDamageBonus(resonance));

					if (event.getEntity().getRemainingFireTicks() < burnTime)
						event.getEntity().setRemainingFireTicks(burnTime);

					if (event.getEntity().level() instanceof ServerLevel serverLevel) {
						serverLevel.sendParticles(GlowParticleOptions.EMBER, event.getEntity().getX(), event.getEntity().getY() + event.getEntity().getEyeHeight() / 1.5, event.getEntity().getZ(), 30, 0.15, 0.15, 0.15, 0.3);
					}
					EmberInventoryUtil.removeEmber(player, cost);
					event.setAmount(event.getAmount() + extraDamage);
				}
			}
		}
	}
}
