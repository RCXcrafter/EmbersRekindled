package com.rekindled.embers.entity;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.projectile.EffectDamage;
import com.rekindled.embers.datagen.EmbersDamageTypes;
import com.rekindled.embers.datagen.EmbersSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class AncientGolemEntity extends Monster {

	public AncientGolemEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
		super(pEntityType, pLevel);
		this.xpReward = 10;
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
		this.addBehaviourGoals();
	}

	protected void addBehaviourGoals() {
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 0.46D, false));
		this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.46D));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.FOLLOW_RANGE, 32.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.5D)
				.add(Attributes.ATTACK_DAMAGE, 6.0D)
				.add(Attributes.MAX_HEALTH, 30.0D)
				.add(Attributes.ARMOR, 6.0D)
				.add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
	}

	public boolean checkSpawnObstruction(LevelReader level) {
		BlockPos pos = new BlockPos(Mth.floor(position().x()), Mth.floor(position().y() - 0.5f), Mth.floor(position().z()));
		return super.checkSpawnObstruction(level) && level.getBlockState(pos).isCollisionShapeFullBlock(level, pos);
	}

	public void tick() {
		super.tick();
		//this.yBodyRot = this.yHeadRot;
		if (!this.isRemoved() && this.getHealth() > 0 && this.tickCount % 100 == 0 && this.getTarget() != null) {
			if (!level().isClientSide()) {
				playSound(EmbersSounds.FIREBALL.get(), 1.0f, 1.0f);
				EmberProjectileEntity proj = RegistryManager.EMBER_PROJECTILE.get().create(level());
				DamageSource damage = new DamageSource(this.level().registryAccess().registry(Registries.DAMAGE_TYPE).get().getHolderOrThrow(EmbersDamageTypes.EMBER_KEY), proj, this);
				EffectDamage effect = new EffectDamage(4.0f, e -> damage, 1, 1.0f);

				Vec3 lookVec = getLookAngle();
				proj.shoot(lookVec.x, lookVec.y, lookVec.z, 0.5f, 0.0f, 4.0f);
				proj.setPos(getEyePosition());
				proj.setEffect(effect);

				level().addFreshEntity(proj);
			}
		}
	}

	public boolean doHurtTarget(Entity pEntity) {
		if (super.doHurtTarget(pEntity)) {
			this.playSound(EmbersSounds.ANCIENT_GOLEM_PUNCH.get());
			return true;
		}
		return false;
	}

	protected SoundEvent getHurtSound(DamageSource pDamageSource) {
		return EmbersSounds.ANCIENT_GOLEM_HURT.get();
	}

	protected SoundEvent getDeathSound() {
		return EmbersSounds.ANCIENT_GOLEM_DEATH.get();
	}

	protected void playStepSound(BlockPos pos, BlockState state) {
		super.playStepSound(pos, state);
		this.playSound(EmbersSounds.ANCIENT_GOLEM_STEP.get());
	}
}
