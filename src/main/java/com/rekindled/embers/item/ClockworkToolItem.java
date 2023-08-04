package com.rekindled.embers.item;

import com.rekindled.embers.api.item.IEmberChargedTool;
import com.rekindled.embers.particle.GlowParticleOptions;
import com.rekindled.embers.util.EmberInventoryUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ClockworkToolItem extends DiggerItem implements IEmberChargedTool {

	public ClockworkToolItem(float attackDamageModifier, float attackSpeedModifier, Tier tier, TagKey<Block> blocks, Properties properties) {
		super(attackDamageModifier, attackSpeedModifier, tier, blocks, properties);
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
		if (hasEmber(stack)) {
			entity.setSecondsOnFire(1);
			return false;
		}
		return true;
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		if (hasEmber(stack)) {
			return super.getDestroySpeed(stack, state);
		}
		return 0;
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.getOrCreateTag().putBoolean("didUse", true);
		if (target.level() instanceof ServerLevel serverLevel) {
			serverLevel.sendParticles(GlowParticleOptions.EMBER, target.getX(), target.getY() + target.getEyeHeight() / 1.5, target.getZ(), 70, 0.15, 0.15, 0.15, 0.6);
		}
		return super.hurtEnemy(stack, target, attacker);
	}

	@Override
	public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entityLiving) {
		stack.getOrCreateTag().putBoolean("didUse", true);
		return super.mineBlock(stack, level, state, pos, entityLiving);
	}

	@Override
	public boolean isEnchantable(ItemStack pStack) {
		return true;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchant){
		return enchant.category != EnchantmentCategory.BREAKABLE && enchant != Enchantments.SWEEPING_EDGE;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		if (oldStack.hasTag() && newStack.hasTag()) {
			return slotChanged || oldStack.getTag().getBoolean("poweredOn") != newStack.getTag().getBoolean("poweredOn") || newStack.getItem() != oldStack.getItem();
		}
		return slotChanged || newStack.getItem() != oldStack.getItem();
	}

	@Override
	public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
		if (oldStack.hasTag() && newStack.hasTag()) {
			return oldStack.getTag().getBoolean("poweredOn") != newStack.getTag().getBoolean("poweredOn");
		}
		return false;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
		if (!selected)
			return;
		if (!stack.hasTag()) {
			stack.getOrCreateTag();
			stack.getTag().putInt("tickCount", 0);
			stack.getTag().putBoolean("poweredOn", false);
			stack.getTag().putBoolean("didUse", false);
		} else {
			if (entity instanceof Player player) {
				stack.getTag().putInt("tickCount", stack.getTag().getInt("tickCount")+1);
				if (stack.getTag().getInt("tickCount") >= 5){
					stack.getTag().putInt("tickCount", 0);
					if (EmberInventoryUtil.getEmberTotal(player) > 5.0) {
						if (!stack.getTag().getBoolean("poweredOn")) {
							stack.getTag().putBoolean("poweredOn", true);
						}
					} else {
						if (stack.getTag().getBoolean("poweredOn")) {
							stack.getTag().putBoolean("poweredOn", false);
						}
					}
				}
				if (stack.getTag().getBoolean("didUse")) {
					stack.getTag().putBoolean("didUse", false);
					EmberInventoryUtil.removeEmber(player, 5.0);
					if (EmberInventoryUtil.getEmberTotal(player) < 5.0) {
						stack.getTag().putBoolean("poweredOn", false);
					}
				}
			}
		}
	}

	@Override
	public boolean hasEmber(ItemStack stack) {
		return stack.hasTag() && stack.getTag().getBoolean("poweredOn");
	}
}
