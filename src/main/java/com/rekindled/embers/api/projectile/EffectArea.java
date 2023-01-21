package com.rekindled.embers.api.projectile;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class EffectArea implements IProjectileEffect {
	IProjectileEffect effect;
	double radius;
	boolean activateOnFizzle;

	public EffectArea(IProjectileEffect effect, double radius, boolean activateOnFizzle) {
		this.effect = effect;
		this.radius = radius;
		this.activateOnFizzle = activateOnFizzle;
	}

	public IProjectileEffect getEffect() {
		return effect;
	}

	public void setEffect(IProjectileEffect effect) {
		this.effect = effect;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public boolean doesActivateOnFizzle() {
		return activateOnFizzle;
	}

	public void setActivateOnFizzle(boolean activateOnFizzle) {
		this.activateOnFizzle = activateOnFizzle;
	}

	@Override
	public void onHit(Level world, HitResult raytrace, IProjectilePreset projectile) {
		Vec3 pos = raytrace.getLocation();
		doAreaEffect(world, projectile, pos);
	}

	@Override
	public void onFizzle(Level world, Vec3 pos, @Nullable IProjectilePreset projectile) {
		if(activateOnFizzle)
			doAreaEffect(world, projectile, pos);
	}

	private void doAreaEffect(Level world, IProjectilePreset projectile, Vec3 pos) {
		AABB aabb = new AABB(pos.x - radius, pos.y - radius, pos.z - radius, pos.x + radius, pos.y + radius, pos.z + radius);
		List<Entity> entities = world.getEntities(projectile.getShooter(), aabb, entity -> true);
		for (Entity entity : entities) {
			effect.onHit(world, new EntityHitResult(entity),projectile);
		}
	}
}
