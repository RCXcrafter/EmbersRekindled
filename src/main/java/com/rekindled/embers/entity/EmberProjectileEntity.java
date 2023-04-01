package com.rekindled.embers.entity;

import java.awt.Color;
import java.util.List;
import java.util.function.Predicate;

import com.rekindled.embers.api.projectile.IProjectileEffect;
import com.rekindled.embers.api.projectile.IProjectilePreset;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.particle.GlowParticleOptions;
import com.rekindled.embers.util.Misc;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

public class EmberProjectileEntity extends Projectile {

	public static final EntityDataAccessor<Float> value = SynchedEntityData.defineId(EmberProjectileEntity.class, EntityDataSerializers.FLOAT);
	public static final EntityDataAccessor<Boolean> dead = SynchedEntityData.defineId(EmberProjectileEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Integer> lifetime = SynchedEntityData.defineId(EmberProjectileEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> color = SynchedEntityData.defineId(EmberProjectileEntity.class, EntityDataSerializers.INT);
	//public UUID id = null;
	public IProjectileEffect effect;
	private IProjectilePreset preset;
	double gravity;

	int homingTime;
	double homingRange;
	int homingIndex, homingModulo; //For spread homing
	Entity homingTarget;
	Predicate<Entity> homingPredicate;

	public EmberProjectileEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
		super(pEntityType, pLevel);
	}

	@Override
	protected void defineSynchedData() {
		getEntityData().define(value, 0f);
		getEntityData().define(dead, false);
		getEntityData().define(lifetime, 160);
		getEntityData().define(color, new Color(255,64,16).getRGB());
	}

	public void shootFromRotation(Entity shooter, float x, float y, float z, float velocity, float inaccuracy, double value) {
		this.dimensions = EntityDimensions.scalable((float) value / 10.0f, (float) value / 10.0f);
		getEntityData().set(EmberProjectileEntity.value, (float) value);
		this.setOwner(shooter);

		super.shootFromRotation(shooter, x, y, z, velocity, inaccuracy);
	}

	public void shoot(double x, double y, double z, float velocity, float inaccuracy, double value) {
		this.dimensions = EntityDimensions.scalable((float) value / 10.0f, (float) value / 10.0f);
		getEntityData().set(EmberProjectileEntity.value, (float) value);

		super.shoot(x, y, z, velocity, inaccuracy);
	}

	public void setGravity(double gravity) {
		this.gravity = gravity;
	}

	public void setColor(int red, int green, int blue, int alpha) {
		getEntityData().set(color, new Color((red * alpha) / 255,(green * alpha) / 255,(blue * alpha) / 255).getRGB());
	}

	public void setHoming(int time, double range, int index, int modulo, Predicate<Entity> predicate) {
		homingTime = time;
		homingRange = range;
		homingIndex = index;
		homingModulo = modulo;
		homingPredicate = predicate;
	}

	public void setPreset(IProjectilePreset preset) {
		this.preset = preset;
	}

	public void setEffect(IProjectileEffect effect) {
		this.effect = effect;
	}

	public void setLifetime(int lifetime) {
		getEntityData().set(EmberProjectileEntity.lifetime, lifetime);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag nbt) {
		getEntityData().set(value, nbt.getFloat("value"));
		getEntityData().set(color, nbt.getInt("color"));
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag nbt) {
		nbt.putFloat("value", getEntityData().get(value));
		nbt.putInt("color", getEntityData().get(color));
	}

	public void tick() {
		super.tick();
		int lifetime = getEntityData().get(EmberProjectileEntity.lifetime);
		getEntityData().set(EmberProjectileEntity.lifetime, lifetime - 1);
		if (lifetime <= 0) {
			this.remove(RemovalReason.DISCARDED);
		}
		if (!getEntityData().get(dead)) {
			getEntityData().set(value, getEntityData().get(value) - 0.025f);
			if (getEntityData().get(value) <= 0) {
				this.remove(RemovalReason.DISCARDED);
			}

			Vec3 currPosVec = this.position();
			Vec3 newPosVector = currPosVec.add(getDeltaMovement());
			HitResult raytraceresult = level.clip(new ClipContext(currPosVec, newPosVector, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));

			if (raytraceresult != null && raytraceresult.getType() != HitResult.Type.MISS)
				newPosVector = raytraceresult.getLocation();

			HitResult hitEntity = ProjectileUtil.getEntityHitResult(level, this, currPosVec, newPosVector, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), this::canHitEntity);

			if (hitEntity != null) {
				newPosVector = hitEntity.getLocation();
				raytraceresult = hitEntity;
			}

			move(MoverType.SELF, newPosVector.subtract(currPosVec));

			setDeltaMovement(getDeltaMovement().add(0, gravity, 0));

			if (!level.isClientSide && raytraceresult != null && raytraceresult.getType() != HitResult.Type.MISS && !ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
				onHit(raytraceresult);
			}

			handleHoming(lifetime, level);

			if (level.isClientSide) {
				double deltaX = getX() - currPosVec.x;
				double deltaY = getY() - currPosVec.y;
				double deltaZ = getZ() - currPosVec.z;
				double dist = Math.ceil(Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 10);
				GlowParticleOptions options = new GlowParticleOptions(Misc.colorFromInt(getEntityData().get(color)), getEntityData().get(value) / 1.75f);
				for (double i = 0; i < dist; i++) {
					double coeff = i / dist;
					level.addParticle(options, currPosVec.x + deltaX * coeff, currPosVec.y + deltaY * coeff, currPosVec.z + deltaZ * coeff, 0.125f*(random.nextFloat()-0.5f), 0.125f*(random.nextFloat()-0.5f), 0.125f*(random.nextFloat()-0.5f));
				}
			}
		} else {
			setDeltaMovement(Vec3.ZERO);
		}
	}

	private void handleHoming(int lifetime, Level world) {
		if (homingTime > 0) {
			if (!isTargetInvalid(homingTarget)) {
				double targetX = homingTarget.getX();
				double targetY = homingTarget.getY()+homingTarget.getBbHeight()/2;
				double targetZ = homingTarget.getZ();
				Vec3 targetVector = new Vec3(targetX-getX(),targetY-getY(),targetZ-getZ());
				double length = targetVector.length();
				targetVector = targetVector.scale(0.3/length);
				double weight  = 0;
				if (length <= homingRange){
					weight = 0.9*((homingRange-length)/homingRange);
				}
				Vec3 delta = this.getDeltaMovement();
				this.setDeltaMovement((0.9-weight)*delta.x+(0.1+weight)*targetVector.x, (0.9-weight)*delta.y+(0.1+weight)*targetVector.y, (0.9-weight)*delta.z+(0.1+weight)*targetVector.z);
				homingTime--;
			} else if (lifetime % 5 == 0) {
				AABB homingAABB = new AABB(getX() - homingRange, getY() - homingRange, getZ() - homingRange, getX() + homingRange, getY() + homingRange, getZ() + homingRange);
				List<Entity> entities = world.getEntities(this, homingAABB, homingPredicate);
				Entity badTarget = null;
				for (Entity entity : entities) {
					long leastSignificantBits = entity.getUUID().getLeastSignificantBits() & 0xFFFF;
					if (leastSignificantBits % homingModulo == homingIndex % homingModulo) {
						homingTarget = entity;
					}
					badTarget = entity;
				}
				if(homingTarget == null)
					homingTarget = badTarget;
			}
		}
	}

	private boolean isTargetInvalid(Entity entity) {
		return entity == null || entity.isRemoved();
	}

	public void onHit(HitResult raytraceresult) {
		super.onHit(raytraceresult);
		playSound(getEntityData().get(value) > 7.0 ? EmbersSounds.FIREBALL_BIG_HIT.get() : EmbersSounds.FIREBALL_HIT.get());

		GlowParticleOptions options = new GlowParticleOptions(Misc.colorFromInt(getEntityData().get(color)), getEntityData().get(value));
		if (level instanceof ServerLevel serverLevel) {
			float dist = ((float)getEntityData().get(value)/3.5f)*0.125f;
			serverLevel.sendParticles(options, getX(), getY(), getZ(), 40, dist, dist, dist, 0.75);
		}

		getEntityData().set(lifetime, 20);
		getEntityData().set(dead, true);

		//double aoeRadius = getEntityData().get(value) * 0.125; //TODO

		if(effect != null)
			effect.onHit(level, raytraceresult, preset);
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		return new ClientboundAddEntityPacket(this);
	}
}
