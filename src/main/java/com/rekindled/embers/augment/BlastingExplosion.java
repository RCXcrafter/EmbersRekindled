package com.rekindled.embers.augment;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Sets;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

public class BlastingExplosion extends Explosion {

	public List<Entity> entitiesToBlast;
	public float damage;
	public Entity source;


	public BlastingExplosion(List<Entity> entitiesToBlast, float damage, Level pLevel, @Nullable Entity pSource, double pToBlowX, double pToBlowY, double pToBlowZ, float pRadius, List<BlockPos> pPositions) {
		super(pLevel, pSource, pToBlowX, pToBlowY, pToBlowZ, pRadius, pPositions);
		this.entitiesToBlast = entitiesToBlast;
		this.damage = damage;
		this.source = pSource;
	}

	public BlastingExplosion(List<Entity> entitiesToBlast, float damage, Level pLevel, @Nullable Entity pSource, double pToBlowX, double pToBlowY, double pToBlowZ, float pRadius, boolean pFire, Explosion.BlockInteraction pBlockInteraction, List<BlockPos> pPositions) {
		super(pLevel, pSource, pToBlowX, pToBlowY, pToBlowZ, pRadius, pFire, pBlockInteraction, pPositions);
		this.entitiesToBlast = entitiesToBlast;
		this.damage = damage;
		this.source = pSource;
	}

	public BlastingExplosion(List<Entity> entitiesToBlast, float damage, Level pLevel, @Nullable Entity pSource, double pToBlowX, double pToBlowY, double pToBlowZ, float pRadius, boolean pFire, Explosion.BlockInteraction pBlockInteraction) {
		super(pLevel, pSource, pToBlowX, pToBlowY, pToBlowZ, pRadius, pFire, pBlockInteraction);
		this.entitiesToBlast = entitiesToBlast;
		this.damage = damage;
		this.source = pSource;
	}

	public BlastingExplosion(List<Entity> entitiesToBlast, float damage, Level pLevel, Entity pSource, DamageSource pDamageSource, ExplosionDamageCalculator pDamageCalculator, double pToBlowX, double pToBlowY, double pToBlowZ, float pRadius, boolean pFire, BlockInteraction pBlockInteraction) {
		super(pLevel, pSource, pDamageSource, pDamageCalculator, pToBlowX, pToBlowY, pToBlowZ, pRadius, pFire, pBlockInteraction);
		this.entitiesToBlast = entitiesToBlast;
		this.damage = damage;
		this.source = pSource;
	}

	public void explode() {
		this.level.gameEvent(this.source, GameEvent.EXPLODE, new Vec3(this.x, this.y, this.z));
		Set<BlockPos> set = Sets.newHashSet();
		int i = 16;

		for (int j = 0; j < i; ++j) {
			for (int k = 0; k < i; ++k) {
				for (int l = 0; l < i; ++l) {
					if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
						double d0 = (double)((float)j / 15.0F * 2.0F - 1.0F);
						double d1 = (double)((float)k / 15.0F * 2.0F - 1.0F);
						double d2 = (double)((float)l / 15.0F * 2.0F - 1.0F);
						double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
						d0 /= d3;
						d1 /= d3;
						d2 /= d3;
						float f = this.radius * (0.7F + this.level.random.nextFloat() * 0.6F);
						double d4 = this.x;
						double d6 = this.y;
						double d8 = this.z;

						for(float f1 = 0.3F; f > 0.0F; f -= 0.22500001F) {
							BlockPos blockpos = BlockPos.containing(d4, d6, d8);
							BlockState blockstate = this.level.getBlockState(blockpos);
							FluidState fluidstate = this.level.getFluidState(blockpos);
							if (!this.level.isInWorldBounds(blockpos)) {
								break;
							}

							Optional<Float> optional = this.damageCalculator.getBlockExplosionResistance(this, this.level, blockpos, blockstate, fluidstate);
							if (optional.isPresent()) {
								f -= (optional.get() + f1) * f1;
							}

							if (f > 0.0F && this.damageCalculator.shouldBlockExplode(this, this.level, blockpos, blockstate, f)) {
								set.add(blockpos);
							}

							d4 += d0 * f1;
							d6 += d1 * f1;
							d8 += d2 * f1;
						}
					}
				}
			}
		}

		this.toBlow.addAll(set);
		float f2 = this.radius * 2.0F;
		ForgeEventFactory.onExplosionDetonate(this.level, this, entitiesToBlast, f2);
		Vec3 vec3 = new Vec3(this.x, this.y, this.z);

		for (int k2 = 0; k2 < entitiesToBlast.size(); ++k2) {
			Entity entity = entitiesToBlast.get(k2);
			if (!entity.ignoreExplosion() && (source == null || !Objects.equals(entity.getUUID(), source.getUUID()))) {
				double d12 = Math.sqrt(entity.distanceToSqr(vec3)) / (double)f2;
				if (d12 <= 1.0D) {
					double d5 = entity.getX() - this.x;
					double d7 = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - this.y;
					double d9 = entity.getZ() - this.z;
					double d13 = Math.sqrt(d5 * d5 + d7 * d7 + d9 * d9);
					if (d13 != 0.0D) {
						d5 /= d13;
						d7 /= d13;
						d9 /= d13;
						double d14 = (double)getSeenPercent(vec3, entity);
						double d10 = (1.0D - d12) * d14;
						entity.hurt(this.getDamageSource(), damage);
						if (entity instanceof LivingEntity living)
							living.hurtTime = 0;
						double d11;
						if (entity instanceof LivingEntity) {
							LivingEntity livingentity = (LivingEntity)entity;
							d11 = ProtectionEnchantment.getExplosionKnockbackAfterDampener(livingentity, d10);
						} else {
							d11 = d10;
						}

						d5 *= d11;
						d7 *= d11;
						d9 *= d11;
						Vec3 vec31 = new Vec3(d5, d7, d9);
						entity.setDeltaMovement(entity.getDeltaMovement().add(vec31));
						if (entity instanceof Player) {
							Player player = (Player)entity;
							if (!player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
								this.hitPlayers.put(player, vec31);
							}
						}
					}
				}
			}
		}
	}
}
