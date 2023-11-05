package com.rekindled.embers.item;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.entity.GlimmerProjectileEntity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public class GlimmerLampItem extends Item {

	public GlimmerLampItem(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
		if (!world.isClientSide() && world.getGameTime() % 10 == 0 && world.getBrightness(LightLayer.SKY, entity.blockPosition()) - world.getSkyDarken() > 12) {
			stack.setDamageValue(stack.getDamageValue() -1);
		}
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged || !ItemStack.isSameItem(oldStack, newStack);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide()) {
			GlimmerProjectileEntity light = RegistryManager.GLIMMER_PROJECTILE.get().create(level);
			double handmod = hand == InteractionHand.MAIN_HAND ? 1.0 : -1.0;
			handmod *= player.getMainArm() == HumanoidArm.RIGHT ? 1.0 : -1.0;
			double posX = player.position().x + player.getLookAngle().x + handmod * (player.getBbWidth() / 2.0) * Math.sin(Math.toRadians(-player.getYHeadRot() - 90));
			double posY = player.position().y + player.getEyeHeight() - 0.2 + player.getLookAngle().y;
			double posZ = player.position().z + player.getLookAngle().z + handmod * (player.getBbWidth() / 2.0) * Math.cos(Math.toRadians(-player.getYHeadRot() - 90));
			light.setPos(posX, posY, posZ);
			light.setDeltaMovement(player.getLookAngle());
			light.setOwner(player);
			level.addFreshEntity(light);
		}
		stack.hurtAndBreak(10, player, e -> {});
		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return true;
	}

	@Override
	public int getBarColor(ItemStack pStack) {
		return 0xFFFFFF;
	}
}
