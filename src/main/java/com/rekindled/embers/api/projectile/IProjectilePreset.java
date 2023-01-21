package com.rekindled.embers.api.projectile;

import java.awt.Color;

import javax.annotation.Nullable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface IProjectilePreset {
	Vec3 getPos();

	Vec3 getVelocity();

	Color getColor();

	IProjectileEffect getEffect();

	@Nullable
	Entity getEntity();

	@Nullable
	Entity getShooter();

	void setPos(Vec3 pos);

	void setVelocity(Vec3 velocity);

	void setColor(Color color);

	void setEffect(IProjectileEffect effect);

	void shoot(Level world);
}
