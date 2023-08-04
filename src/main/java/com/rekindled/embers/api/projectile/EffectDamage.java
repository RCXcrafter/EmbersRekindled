package com.rekindled.embers.api.projectile;

import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class EffectDamage implements IProjectileEffect {
	float damage;
	Function<Entity, DamageSource> source;
	int fire;
	double invinciblityMultiplier = 1.0;

	public EffectDamage(float damage, Function<Entity, DamageSource> source, int fire, double invinciblityMultiplier) {
		this.damage = damage;
		this.source = source;
		this.fire = fire;
		this.invinciblityMultiplier = invinciblityMultiplier;
	}

	public float getDamage() {
		return damage;
	}

	public void setDamage(float damage) {
		this.damage = damage;
	}

	public Function<Entity, DamageSource> getSource() {
		return source;
	}

	public void setSource(Function<Entity, DamageSource> source) {
		this.source = source;
	}

	public int getFire() {
		return fire;
	}

	public void setFire(int seconds) {
		this.fire = seconds;
	}

	public double getInvinciblityMultiplier() {
		return invinciblityMultiplier;
	}

	public void setInvinciblityMultiplier(double multiplier) {
		this.invinciblityMultiplier = multiplier;
	}

	@Override
	public void onEntityImpact(Entity entity, @Nullable IProjectilePreset projectile) {
		Entity shooter = projectile != null ? projectile.getShooter() : null;
		Entity projectileEntity = projectile != null ? projectile.getEntity() : null;
		if (entity.hurt(source.apply(projectileEntity), damage))
			entity.setSecondsOnFire(fire);
		if (entity instanceof LivingEntity) {
			LivingEntity livingTarget = (LivingEntity) entity;
			livingTarget.setLastHurtMob(shooter);
			livingTarget.hurtDuration = (int) (livingTarget.hurtDuration * invinciblityMultiplier);
		}
	}
}
