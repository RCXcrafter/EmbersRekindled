package com.rekindled.embers.api.event;

import java.util.List;

import com.google.common.collect.Lists;
import com.rekindled.embers.api.projectile.IProjectilePreset;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

public class EmberProjectileEvent extends Event {
	private LivingEntity shooter;
	private ItemStack stack;
	private List<IProjectilePreset> projectiles;
	private double charge;

	public EmberProjectileEvent(LivingEntity shooter, ItemStack stack, double charge, List<IProjectilePreset> projectiles) {
		this.shooter = shooter;
		this.stack = stack;
		this.projectiles = projectiles;
		this.charge = charge;
	}

	public EmberProjectileEvent(LivingEntity shooter, ItemStack stack, double charge, IProjectilePreset... projectiles) {
		this(shooter, stack, charge, Lists.newArrayList(projectiles));
	}

	public ItemStack getStack() {
		return stack;
	}

	public LivingEntity getShooter() {
		return shooter;
	}

	public List<IProjectilePreset> getProjectiles() {
		return projectiles;
	}

	public double getCharge() {
		return charge;
	}

	@Override
	public boolean isCancelable() {
		return true;
	}
}
