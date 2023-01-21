package com.rekindled.embers.damage;

import java.util.function.Function;

import com.rekindled.embers.api.projectile.IProjectilePreset;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;

public class DamageEmber extends DamageSource {

	public static Function<IProjectilePreset, DamageSource> EMBER_DAMAGE_SOURCE_FACTORY = (projectile) -> {
		if(projectile == null)
			return new DamageSource("ember").setMagic();
		else if(projectile.getEntity() == null)
			return new EntityDamageSource("ember", projectile.getShooter()).setMagic();
		else
			return new IndirectEntityDamageSource("ember", projectile.getEntity(), projectile.getShooter()).setMagic();
	};

	public DamageEmber() {
		super("ember");
		this.setMagic();
	}
}
