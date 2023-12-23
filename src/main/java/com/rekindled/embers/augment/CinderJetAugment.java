package com.rekindled.embers.augment;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.rekindled.embers.api.augment.AugmentUtil;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.particle.SmokeParticleOptions;
import com.rekindled.embers.util.EmberInventoryUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CinderJetAugment extends AugmentBase {

	public CinderJetAugment(ResourceLocation id) {
		super(id, 2.0);
		MinecraftForge.EVENT_BUS.register(this);
	}

	public static Map<UUID, Boolean> sprintingClient = new HashMap<UUID, Boolean>();
	public static Map<UUID, Boolean> sprintingServer = new HashMap<UUID, Boolean>();

	public static Map<UUID, Boolean> getSprinting(Level level) {
		if (level.isClientSide())
			return sprintingClient;
		return sprintingServer;
	}

	@SubscribeEvent
	public void onLivingTick(LivingTickEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity instanceof Player player && (!entity.level().isClientSide() || entity == Minecraft.getInstance().player)) {
			UUID id = entity.getUUID();
			if (getSprinting(entity.level()).containsKey(id)) {
				if (entity.isSprinting() && !getSprinting(entity.level()).get(id)) {
					int level = AugmentUtil.getArmorAugmentLevel(player, this);
					float dashStrength = (float)(2.0*(Math.atan(0.6*(level))/(1.25)));
					if (dashStrength > 0 && entity.onGround() && EmberInventoryUtil.getEmberTotal(player) > cost) {
						EmberInventoryUtil.removeEmber((player), cost);
						entity.setDeltaMovement(entity.getDeltaMovement().add(new Vec3(2.0* entity.getLookAngle().x*dashStrength, 0.4, 2.0* entity.getLookAngle().z*dashStrength)));
						entity.level().playSound(player, entity, EmbersSounds.CINDER_JET.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
						if (entity.level() instanceof ServerLevel serverLevel) {
							serverLevel.sendParticles(SmokeParticleOptions.BIG_SMOKE, entity.getX() - entity.getLookAngle().x * 0.5f, entity.getY() + entity.getBbHeight() / 4.0f, entity.getZ() - (float) entity.getLookAngle().z * 0.5f, 40, 0.1, 0.1, 0.1, 1.0);
						}
					}
				}
				getSprinting(entity.level()).replace(id, entity.isSprinting());
			} else {
				getSprinting(entity.level()).put(id, entity.isSprinting());
			}
		}
	}
}
