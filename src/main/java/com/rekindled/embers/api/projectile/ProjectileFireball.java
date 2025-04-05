package com.rekindled.embers.api.projectile;

import java.awt.Color;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import org.joml.Vector3f;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.entity.EmberProjectileEntity;
import com.rekindled.embers.util.EmbersColors;
import com.rekindled.embers.util.Misc;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ProjectileFireball implements IProjectilePreset {
	Vec3 pos;
	Vec3 velocity;
	IProjectileEffect effect;
	double size;
	int lifetime;
	Entity shooter;
	EmberProjectileEntity entity;
	ResourceLocation colorId = EmbersColors.EMBER_ID;
	Vector3f color = EmbersColors.EMBER;
	double gravity;

	int homingTime;
	double homingRange;
	int homingIndex, homingModulo;
	Predicate<Entity> homingPredicate;

	public ProjectileFireball(Entity shooter, Vec3 pos, Vec3 velocity, double size, int lifetime, IProjectileEffect effect) {
		this.pos = pos;
		this.velocity = velocity;
		this.effect = effect;
		this.size = size;
		this.lifetime = lifetime;
		this.shooter = shooter;
	}

	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public int getLifetime() {
		return lifetime;
	}

	public void setLifetime(int lifetime) {
		this.lifetime = lifetime;
	}

	@Override
	public Vec3 getPos() {
		return pos;
	}

	@Override
	public void setPos(Vec3 pos) {
		this.pos = pos;
	}

	@Override
	public Vec3 getVelocity() {
		return velocity;
	}

	@Override
	public void setVelocity(Vec3 velocity) {
		this.velocity = velocity;
	}

	@Override
	public Vector3f getColor() {
		return this.color;
	}

	@Override
	public ResourceLocation getColorId() {
		return this.colorId;
	}

	@Override
	public void setColor(Vector3f color) {
		this.color = color;
		this.colorId = EmbersColors.CUSTOM_ID;
	}

	@Override
	public void setColor(ResourceLocation color) {
		this.colorId = color;
	}

	@Override
	@Deprecated
	public void setColor(Color color) {
		setColor(Misc.colorFromInt(color.getRGB()));
	}

	public double getGravity() {
		return gravity;
	}

	public void setGravity(double gravity) {
		this.gravity = gravity;
	}

	@Override
	public IProjectileEffect getEffect() {
		return effect;
	}

	@Nullable
	@Override
	public Entity getEntity() {
		return entity;
	}

	@Nullable
	@Override
	public Entity getShooter() {
		return shooter;
	}

	@Override
	public void setEffect(IProjectileEffect effect) {
		this.effect = effect;
	}

	public void setHoming(int time, double range, int index, int modulo, Predicate<Entity> predicate) {
		homingTime = time;
		homingRange = range;
		homingIndex = index;
		homingModulo = modulo;
		homingPredicate = predicate;
	}

	@Override
	public void shoot(Level world) {
		entity = new EmberProjectileEntity(RegistryManager.EMBER_PROJECTILE.get(), world);
		entity.shootFromRotation(shooter, (float) velocity.x, (float) velocity.y, (float) velocity.z, (float) velocity.length(), 0, size);
		entity.setPos(pos);
		entity.setGravity(gravity);
		entity.setEffect(effect);
		entity.setPreset(this);
		entity.setLifetime(lifetime);
		entity.setColor(color);
		entity.setColor(colorId);
		entity.setHoming(homingTime,homingRange,homingIndex,homingModulo,homingPredicate);
		world.addFreshEntity(entity);
	}
}
