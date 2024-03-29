package com.rekindled.embers.augment;

import com.rekindled.embers.api.augment.AugmentUtil;
import com.rekindled.embers.damage.DamageEmber;
import com.rekindled.embers.datagen.EmbersDamageTypes;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.particle.GlowParticleOptions;
import com.rekindled.embers.util.EmberInventoryUtil;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class FlameBarrierAugment extends AugmentBase {

	public FlameBarrierAugment(ResourceLocation id) {
		super(id, 2.0);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onHit(LivingHurtEvent event) {
		if (event.getEntity() instanceof Player player && event.getSource().getEntity() instanceof LivingEntity) {
			int barrierLevel = AugmentUtil.getArmorAugmentLevel(player, this);

			float strength = (float)(2.0*(Math.atan(0.6*(barrierLevel))/(Math.PI)));
			if (barrierLevel > 0 && EmberInventoryUtil.getEmberTotal(player) >= cost) {
				EmberInventoryUtil.removeEmber(player, cost);
				DamageSource damage = new DamageEmber(player.level().registryAccess().registry(Registries.DAMAGE_TYPE).get().getHolderOrThrow(EmbersDamageTypes.EMBER_KEY), player);
				event.getSource().getEntity().hurt(damage, strength*event.getAmount()*0.5f);
				event.getSource().getEntity().setSecondsOnFire(barrierLevel+1);
				event.getEntity().level().playSound(null, event.getEntity(), EmbersSounds.FIREBALL_HIT.get(), SoundSource.PLAYERS, 1.0f, 1.0f);

				if (event.getEntity().level() instanceof ServerLevel serverLevel) {
					serverLevel.sendParticles(GlowParticleOptions.EMBER, event.getEntity().getX(), event.getEntity().getY() + event.getEntity().getBbHeight() / 2.0, event.getEntity().getZ(), 20, 0.5, 0.5, 0.5, 0.3);
				}
			}
		}
	}
}
