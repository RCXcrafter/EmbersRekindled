package com.rekindled.embers.item;

import java.util.Random;

import com.rekindled.embers.ConfigManager;
import com.rekindled.embers.api.event.EmberProjectileEvent;
import com.rekindled.embers.api.item.IProjectileWeapon;
import com.rekindled.embers.api.projectile.EffectDamage;
import com.rekindled.embers.api.projectile.IProjectilePreset;
import com.rekindled.embers.api.projectile.ProjectileRay;
import com.rekindled.embers.damage.DamageEmber;
import com.rekindled.embers.datagen.EmbersDamageTypes;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.util.EmberInventoryUtil;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;

public class BlazingRayItem extends Item implements IProjectileWeapon {

	public static Random rand = new Random();

	public BlazingRayItem(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
		if (!level.isClientSide) {
			double charge = (Math.min(ConfigManager.BLAZING_RAY_MAX_CHARGE.get(), getUseDuration(stack) - timeLeft)) / (double) ConfigManager.BLAZING_RAY_MAX_CHARGE.get();
			double handmod = entity.getUsedItemHand() == InteractionHand.MAIN_HAND ? 1.0 : -1.0;
			handmod *= entity.getMainArm() == HumanoidArm.RIGHT ? 1.0 : -1.0;
			double posX = entity.getX() + entity.getLookAngle().x + handmod * (entity.getBbWidth() / 2.0) * Math.sin(Math.toRadians(-entity.getYHeadRot() - 90));
			double posY = entity.getY() + entity.getEyeHeight() - 0.2 + entity.getLookAngle().y;
			double posZ = entity.getZ() + entity.getLookAngle().z + handmod * (entity.getBbWidth() / 2.0) * Math.cos(Math.toRadians(-entity.getYHeadRot() - 90));

			double targX = entity.getX() + entity.getLookAngle().x * ConfigManager.BLAZING_RAY_MAX_DISTANCE.get() + (ConfigManager.BLAZING_RAY_MAX_SPREAD.get() * (1.0 - charge) * (rand.nextFloat() - 0.5));
			double targY = entity.getY() + entity.getLookAngle().y * ConfigManager.BLAZING_RAY_MAX_DISTANCE.get() + (ConfigManager.BLAZING_RAY_MAX_SPREAD.get() * (1.0 - charge) * (rand.nextFloat() - 0.5));
			double targZ = entity.getZ() + entity.getLookAngle().z * ConfigManager.BLAZING_RAY_MAX_DISTANCE.get() + (ConfigManager.BLAZING_RAY_MAX_SPREAD.get() * (1.0 - charge) * (rand.nextFloat() - 0.5));

			DamageSource damage = new DamageEmber(level.registryAccess().registry(Registries.DAMAGE_TYPE).get().getHolderOrThrow(EmbersDamageTypes.EMBER_KEY), entity, true);
			EffectDamage effect = new EffectDamage(ConfigManager.BLAZING_RAY_DAMAGE.get().floatValue(), e -> damage, 1, 1.0f);
			ProjectileRay ray = new ProjectileRay(entity, new Vec3(posX, posY, posZ), new Vec3(targX, targY, targZ), false, effect);

			EmberProjectileEvent event = new EmberProjectileEvent(entity, stack, charge, ray);
			MinecraftForge.EVENT_BUS.post(event);
			if (!event.isCanceled()) {
				for (IProjectilePreset projectile : event.getProjectiles()) {
					projectile.shoot(level);
				}
			}

			level.playSound(null, entity, EmbersSounds.BLAZING_RAY_FIRE.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
			stack.getOrCreateTag().putLong("lastUse", level.getGameTime());
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
		if (!stack.hasTag() || stack.getTag().getLong("lastUse") + ConfigManager.BLAZING_RAY_COOLDOWN.get() <= level.getGameTime() || player.isCreative()) {
			if (EmberInventoryUtil.getEmberTotal(player) >= ConfigManager.BLAZING_RAY_COST.get() || player.isCreative()) {
				EmberInventoryUtil.removeEmber(player, ConfigManager.BLAZING_RAY_COST.get());
				player.startUsingItem(hand);
				return InteractionResultHolder.consume(stack);
			} else {
				level.playSound(null, player, EmbersSounds.BLAZING_RAY_EMPTY.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
				return InteractionResultHolder.fail(stack);
			}
		}
		return InteractionResultHolder.pass(stack); //OFFHAND FIRE ENABLED BOYS
	}
}
