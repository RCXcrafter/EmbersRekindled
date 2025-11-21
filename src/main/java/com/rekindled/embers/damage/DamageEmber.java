package com.rekindled.embers.damage;

import javax.annotation.Nullable;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class DamageEmber extends DamageSource {

	boolean indirect = false;

	public DamageEmber(Holder<DamageType> type, @Nullable Entity directEntity, @Nullable Entity causingEntity, @Nullable Vec3 damageSourcePosition) {
		super(type, directEntity, causingEntity, damageSourcePosition);
	}

	public DamageEmber(Holder<DamageType> type, @Nullable Entity directEntity, @Nullable Entity causingEntity) {
		super(type, directEntity, causingEntity, null);
	}

	public DamageEmber(Holder<DamageType> type, Vec3 damageSourcePosition) {
		super(type, null, null, damageSourcePosition);
	}

	public DamageEmber(Holder<DamageType> type, @Nullable Entity entity) {
		super(type, entity, entity);
	}

	public DamageEmber(Holder<DamageType> type, @Nullable Entity entity, boolean indirect) {
		super(type, entity, entity);
		this.indirect = indirect;
	}

	public DamageEmber(Holder<DamageType> type) {
		super(type, null, null, null);
	}

	@Override
	public boolean isIndirect() {
		return indirect || super.isIndirect();
	}

	@Override
	public Component getLocalizedDeathMessage(LivingEntity livingEntity) {
		String s = "death.attack." + this.type().msgId();
		Entity entity = this.getEntity();
		if (entity == null)
			entity = this.getDirectEntity();
		if (entity == null)
			entity = livingEntity.getKillCredit();

		ItemStack itemstack;
		if (entity instanceof LivingEntity living) {
			itemstack = living.getMainHandItem();
		} else {
			itemstack = ItemStack.EMPTY;
		}

		if (entity == null)
			return Component.translatable(s, livingEntity.getDisplayName());
		if (!itemstack.isEmpty() && itemstack.hasCustomHoverName())
			return Component.translatable(s + ".item", livingEntity.getDisplayName(), entity.getDisplayName(), itemstack.getDisplayName());
		return Component.translatable(s + ".player", livingEntity.getDisplayName(), entity.getDisplayName());
	}
}
