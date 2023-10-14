package com.rekindled.embers.api.item;

import javax.annotation.Nullable;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IInflictorGem {
	void attuneSource(ItemStack stack, @Nullable LivingEntity entity, DamageSource source);

	@Nullable
	String getAttunedSource(ItemStack stack);

	float getDamageResistance(ItemStack stack, float modifier);
}
