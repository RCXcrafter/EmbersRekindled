package com.rekindled.embers.damage;

import javax.annotation.Nullable;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
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
		indirect = true;
	}

	public DamageEmber(Holder<DamageType> type) {
		super(type, null, null, null);
	}

	@Override
	public boolean isIndirect() {
		return indirect || super.isIndirect();
	}
}
