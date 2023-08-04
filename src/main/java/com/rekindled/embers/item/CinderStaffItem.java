package com.rekindled.embers.item;

import java.awt.Color;
import java.util.Random;
import java.util.function.Function;

import org.joml.Vector3f;

import com.rekindled.embers.api.event.EmberProjectileEvent;
import com.rekindled.embers.api.event.ItemVisualEvent;
import com.rekindled.embers.api.item.IProjectileWeapon;
import com.rekindled.embers.api.projectile.EffectArea;
import com.rekindled.embers.api.projectile.EffectDamage;
import com.rekindled.embers.api.projectile.IProjectilePreset;
import com.rekindled.embers.api.projectile.ProjectileFireball;
import com.rekindled.embers.datagen.EmbersDamageTypes;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.particle.GlowParticleOptions;
import com.rekindled.embers.util.EmberInventoryUtil;
import com.rekindled.embers.util.Misc;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;

public class CinderStaffItem extends Item implements IProjectileWeapon {

	public static double EMBER_COST = 25.0;
	public static int COOLDOWN = 10;
	public static double MAX_CHARGE = 60;
	public static float DAMAGE = 17;
	public static float SIZE = 17;
	public static float AOE_SIZE = 17 * 0.125f;
	public static int LIFETIME = 160;

	public static boolean soundPlaying = false; //Clientside anyway so whatever
	public static Random rand = new Random();

	public CinderStaffItem(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
		if (!level.isClientSide) {
			double charge = (Math.min(MAX_CHARGE, getUseDuration(stack) - timeLeft)) / MAX_CHARGE;
			float spawnDistance = 2.0f;//Math.max(1.0f, (float)charge/5.0f);
			Vec3 eyesPos = entity.getEyePosition();
			HitResult traceResult = getPlayerPOVHitResult(entity.level(), (Player) entity, ClipContext.Fluid.NONE);
			if (traceResult.getType() == HitResult.Type.BLOCK)
				spawnDistance = (float) Math.min(spawnDistance, traceResult.getLocation().distanceTo(eyesPos));
			Vec3 launchPos = eyesPos.add(entity.getLookAngle().scale(spawnDistance));
			float damage = (float) Math.max(charge * DAMAGE, 0.5f);
			float size = (float) Math.max(charge * SIZE, 0.5f);
			float aoeSize = (float) charge * AOE_SIZE;
			int lifetime = charge * DAMAGE >= 1.0 ? LIFETIME : 5;

			Function<Entity, DamageSource> damageSource = e -> new DamageSource(level.registryAccess().registry(Registries.DAMAGE_TYPE).get().getHolderOrThrow(EmbersDamageTypes.EMBER_KEY), e, entity);
			EffectArea effect = new EffectArea(new EffectDamage(damage, damageSource, 1, 1.0), aoeSize, false);
			ProjectileFireball fireball = new ProjectileFireball(entity, launchPos, entity.getLookAngle().scale(0.85), size, lifetime, effect);
			EmberProjectileEvent event = new EmberProjectileEvent(entity, stack, charge, fireball);
			MinecraftForge.EVENT_BUS.post(event);
			if (!event.isCanceled()) {
				for (IProjectilePreset projectile : event.getProjectiles()) {
					projectile.shoot(level);
				}
			}

			SoundEvent sound;
			if (charge * DAMAGE >= 10.0)
				sound = EmbersSounds.FIREBALL_BIG.get();
			else if (charge * DAMAGE >= 1.0)
				sound = EmbersSounds.FIREBALL.get();
			else
				sound = EmbersSounds.CINDER_STAFF_FAIL.get();
			level.playSound(null, launchPos.x, launchPos.y, launchPos.z, sound, SoundSource.PLAYERS, 1.0f, 1.0f);
		}
		stack.getOrCreateTag().putLong("lastUse", level.getGameTime());
		entity.swing(entity.getUsedItemHand());
		entity.stopUsingItem();
	}

	@Override
	public void onUseTick(Level level, LivingEntity player, ItemStack stack, int count) {
		if (stack.hasTag() && stack.getTag().getLong("lastUse") + COOLDOWN > level.getGameTime())
			player.stopUsingItem();
		double charge = ((Math.min(60, 72000 - count)) / 60.0) * 15.0;
		boolean fullCharge = charge >= 15.0;
		ItemVisualEvent event = new ItemVisualEvent(player, Misc.handToSlot(player.getUsedItemHand()),stack,new Color(255,64,16),fullCharge ? EmbersSounds.CINDER_STAFF_LOOP.get() : null, 1.0f, 1.0f, "charge");

		MinecraftForge.EVENT_BUS.post(event);

		if (event.hasSound()) {
			if (!soundPlaying) {
				if (level.isClientSide())
					EmbersSounds.playItemSoundClient(player, this, event.getSound(), SoundSource.PLAYERS, true, event.getVolume(), event.getPitch());
				soundPlaying = true;
			}
		} else {
			soundPlaying = false;
		}

		if (event.hasParticles()) {
			Color color = event.getColor();
			float spawnDistance = 2.0f;//Math.max(1.0f, (float)charge/5.0f);
			Vec3 eyesPos = player.getEyePosition();
			HitResult traceResult = getPlayerPOVHitResult(level, (Player) player, ClipContext.Fluid.NONE);
			if (traceResult.getType() == HitResult.Type.BLOCK)
				spawnDistance = (float) Math.min(spawnDistance, traceResult.getLocation().distanceTo(eyesPos));
			Vec3 launchPos = eyesPos.add(player.getLookAngle().scale(spawnDistance));
			GlowParticleOptions options = new GlowParticleOptions(new Vector3f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F), (float) (charge / 1.75f), 24);
			for (int i = 0; i < 4; i++)
				level.addParticle(options, (float) launchPos.x + (rand.nextFloat() * 0.1f - 0.05f), (float) launchPos.y + (rand.nextFloat() * 0.1f - 0.05f), (float) launchPos.z + (rand.nextFloat() * 0.1f - 0.05f), 0, 0.000001, 0);
		}
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged || !ItemStack.matches(oldStack, newStack);
	}

	@Override
	public int getUseDuration(ItemStack pStack) {
		return 72000;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack pStack) {
		return UseAnim.BOW;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!stack.hasTag() || stack.getTag().getLong("lastUse") + COOLDOWN <= level.getGameTime() || player.isCreative()) {
			if (EmberInventoryUtil.getEmberTotal(player) >= EMBER_COST || player.isCreative()) {
				EmberInventoryUtil.removeEmber(player, EMBER_COST);
				player.startUsingItem(hand);
				if (level.isClientSide())
					EmbersSounds.playItemSoundClient(player, this, EmbersSounds.CINDER_STAFF_CHARGE.get(), SoundSource.PLAYERS, false, 1.0f, 1.0f);
				else
					EmbersSounds.playItemSound(player, this, EmbersSounds.CINDER_STAFF_CHARGE.get(), SoundSource.PLAYERS, false, 1.0f, 1.0f);
				return InteractionResultHolder.consume(stack);
			} else {
				return InteractionResultHolder.fail(stack);
			}
		}
		return InteractionResultHolder.pass(stack); //OFFHAND FIRE ENABLED BOYS
	}
}
