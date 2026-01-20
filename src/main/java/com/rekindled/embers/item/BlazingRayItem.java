package com.rekindled.embers.item;

import java.awt.Color;
import java.util.Random;
import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rekindled.embers.ConfigManager;
import com.rekindled.embers.api.event.EmberProjectileEvent;
import com.rekindled.embers.api.event.ItemVisualEvent;
import com.rekindled.embers.api.item.IProjectileWeapon;
import com.rekindled.embers.api.projectile.EffectDamage;
import com.rekindled.embers.api.projectile.IProjectilePreset;
import com.rekindled.embers.api.projectile.ProjectileRay;
import com.rekindled.embers.damage.DamageEmber;
import com.rekindled.embers.datagen.EmbersDamageTypes;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.particle.VaporParticleOptions;
import com.rekindled.embers.util.EmberInventoryUtil;
import com.rekindled.embers.util.Misc;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
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
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.MinecraftForge;

public class BlazingRayItem extends Item implements IProjectileWeapon , IClientItemExtensions {

	public double  aimingInterpolation = 0f; //used for animations - goes from 0 to 1
	public static boolean soundPlaying = false; //Clientside - similar implementation as the cinder staff
	public static Random rand = new Random();

	public BlazingRayItem(Properties pProperties) {
		super(pProperties);
	}
	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer)
	{
		consumer.accept(new IClientItemExtensions() {

			private static final HumanoidModel.ArmPose AIM = HumanoidModel.ArmPose.create("EXAMPLE", true, (model, entity, arm) -> {
				// modified version of the crossbow hold animation that only uses one arm
				// TODO modify the held angle slightly so the weapon points forward, crossbows are held at a special angle since they are items with a 45degree angle
				boolean isInRightHand = arm == HumanoidArm.RIGHT;
				ModelPart animArm = isInRightHand ? model.rightArm : model.leftArm;
				animArm.yRot = (isInRightHand ? -0.3F : 0.3F) + model.head.yRot;
				animArm.xRot = -1.5707964F + model.head.xRot + 0.1F;
			});

			@Override
			public HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
				if (!itemStack.isEmpty()) {
					if (entityLiving.getUsedItemHand() == hand && entityLiving.getUseItemRemainingTicks() > 0) {
						return AIM;

					}
				}
				return HumanoidModel.ArmPose.EMPTY;
			}

			@Override
			public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm, ItemStack itemInHand, float partialTick, float equipProcess, float swingProcess) {
				int side = arm == HumanoidArm.RIGHT ? 1 : -1;
				poseStack.translate(side * 0.56f, -0.52f, -0.72f); //this is the default hand position
				if (player.getUseItem() == itemInHand && player.isUsingItem()) {
					double interpolation = aimingInterpolation ;
					poseStack.translate(-side * 0.56f * interpolation, 0.0f, -0.25f * interpolation);
				}
				return true;
			}
		});
	}
	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
		if (!level.isClientSide) {
			double charge = (Math.min(ConfigManager.BLAZING_RAY_MAX_CHARGE.get(), getUseDuration(stack) - timeLeft)) / (double) ConfigManager.BLAZING_RAY_MAX_CHARGE.get();

			// moved to getBarrelTip method to avoid duplicated code

			double targX = entity.getX() + entity.getLookAngle().x * ConfigManager.BLAZING_RAY_MAX_DISTANCE.get() + (ConfigManager.BLAZING_RAY_MAX_SPREAD.get() * (1.0 - charge) * (rand.nextFloat() - 0.5));
			double targY = entity.getY() + entity.getLookAngle().y * ConfigManager.BLAZING_RAY_MAX_DISTANCE.get() + (ConfigManager.BLAZING_RAY_MAX_SPREAD.get() * (1.0 - charge) * (rand.nextFloat() - 0.5));
			double targZ = entity.getZ() + entity.getLookAngle().z * ConfigManager.BLAZING_RAY_MAX_DISTANCE.get() + (ConfigManager.BLAZING_RAY_MAX_SPREAD.get() * (1.0 - charge) * (rand.nextFloat() - 0.5));

			DamageSource damage = new DamageEmber(level.registryAccess().registry(Registries.DAMAGE_TYPE).get().getHolderOrThrow(EmbersDamageTypes.EMBER_KEY), entity, true);
			EffectDamage effect = new EffectDamage(ConfigManager.BLAZING_RAY_DAMAGE.get().floatValue(), e -> damage, 1, 1.0f);
			ProjectileRay ray = new ProjectileRay(entity, getBarrelTip(entity), new Vec3(targX, targY, targZ), false, effect);

			EmberProjectileEvent event = new EmberProjectileEvent(entity, stack, charge, ray);
			MinecraftForge.EVENT_BUS.post(event);
			if (!event.isCanceled()) {
				for (IProjectilePreset projectile : event.getProjectiles()) {
					projectile.shoot(level);
				}
			}

			level.playSound(null, entity, EmbersSounds.BLAZING_RAY_FIRE.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
			stack.getOrCreateTag().putLong("lastUse", level.getGameTime());
			aimingInterpolation = 0 ; // reset the interpolation back to its initial value
		}
	}
	@Override
	public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int count) {

		if (stack.hasTag() && stack.getTag().getLong("lastUse") + ConfigManager.BLAZING_RAY_COOLDOWN.get() > level.getGameTime() && !(entity instanceof Player && ((Player) entity).isCreative()))
			entity.stopUsingItem();
		double charge = (Math.min(ConfigManager.BLAZING_RAY_MAX_CHARGE.get(), getUseDuration(stack) - count)) / (double) ConfigManager.BLAZING_RAY_MAX_CHARGE.get();
		boolean fullCharge = charge >= 1.0;
		// TODO add a proper unique loop sound for the Blazing Ray - right now it is just pitched down cinder staff sound
		ItemVisualEvent event = new ItemVisualEvent(entity, Misc.handToSlot(entity.getUsedItemHand()),stack,new Color(200,200,220),fullCharge ? EmbersSounds.CINDER_STAFF_LOOP.get() : null, 0.9f, 1.0f, "charge");
		if (ConfigManager.BLAZING_RAY_AIM_ENABLE.get()){
			aimingInterpolation = (float) Math.min( charge / ConfigManager.BLAZING_RAY_AIM_TIME.get(),1); }

		MinecraftForge.EVENT_BUS.post(event);

		if (event.hasSound()) {
			if (!soundPlaying) {
				if (level.isClientSide())
					EmbersSounds.playItemSoundClient(entity, this, event.getSound(), SoundSource.PLAYERS, true, event.getVolume(), event.getPitch());
				soundPlaying = true;
			}
		} else {
			soundPlaying = false;
		}

		if (event.hasParticles()) {
//			we don't use particleOption for the momentum
			Vec3 rightSteamMomentum = entity.getForward().scale(0.15d).yRot((float) Math.PI/2f); // we make a vector that points on the right side of where the player is looking
			Vec3 leftSteamMomentum = rightSteamMomentum.yRot((float)Math.PI); //we rotate it by 180 degree to get the other direction
//			Color color = event.getColor(); unused the base vapor color is used instead
			Vec3 particlesPos = getBarrelTip(entity); //where the particle spawns
			//TODO add a parameter for vapor particle amount/size
			VaporParticleOptions options = new VaporParticleOptions( VaporParticleOptions.VAPOR_COLOR, new Vec3(0,0,0) ,(float) (charge * ConfigManager.CINDER_STAFF_SIZE.get() / 100.0f));
			for (int i = 0; i < 4; i++) {
				level.addParticle(options, (float) particlesPos.x + (rand.nextFloat() * 0.1f - 0.05f), (float) particlesPos.y + (rand.nextFloat() * 0.1f - 0.05f), (float) particlesPos.z + (rand.nextFloat() * 0.1f - 0.05f), rightSteamMomentum.x, rightSteamMomentum.y, rightSteamMomentum.z);
				level.addParticle(options, (float) particlesPos.x + (rand.nextFloat() * 0.1f - 0.05f), (float) particlesPos.y + (rand.nextFloat() * 0.1f - 0.05f), (float) particlesPos.z + (rand.nextFloat() * 0.1f - 0.05f), leftSteamMomentum.x, leftSteamMomentum.y, leftSteamMomentum.z);
			}
		}
	}
	public Vec3 getBarrelTip(LivingEntity entity){
		double handmod = entity.getUsedItemHand() == InteractionHand.MAIN_HAND ? 1.0 : -1.0;
		handmod *= entity.getMainArm() == HumanoidArm.RIGHT ? 1.0 : -1.0;
		double posX = entity.getX() + entity.getLookAngle().x + (entity.getBbWidth() / 2.0) * Math.sin(Math.toRadians(-entity.getYHeadRot() - 90));
		double posY = entity.getY() + entity.getEyeHeight() - 0.2 + entity.getLookAngle().y;
		double posZ = entity.getZ() + entity.getLookAngle().z + handmod * (entity.getBbWidth() / 2.0) * Math.cos(Math.toRadians(-entity.getYHeadRot() - 90));
		Vec3 startPos = new Vec3(posX, posY, posZ);
		Vec3 endPos = entity.getEyePosition().add(entity.getLookAngle().scale((entity.getBbWidth() / 2.0))).subtract(new Vec3(0, 0.2, 0));
		return  startPos.lerp(endPos, aimingInterpolation);
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
		return UseAnim.CUSTOM;
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
