package com.rekindled.embers.entity;

import org.joml.Vector3f;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.block.GlimmerBlock;
import com.rekindled.embers.particle.SmokeParticleOptions;
import com.rekindled.embers.particle.SparkParticleOptions;
import com.rekindled.embers.util.Misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

public class GlimmerProjectileEntity extends Projectile {

	public static final SparkParticleOptions GLIMMER = new SparkParticleOptions(new Vector3f(255.0F / 255.0F, 128.0F / 255.0F, 16.0F / 255.0F), 1.5F);
	public static final SmokeParticleOptions SMOKE = new SmokeParticleOptions(SmokeParticleOptions.SMOKE_COLOR, 6.0F);
	public static final EntityDataAccessor<Integer> lifetime = SynchedEntityData.defineId(EmberProjectileEntity.class, EntityDataSerializers.INT);

	public GlimmerProjectileEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
		super(pEntityType, pLevel);
		this.noPhysics = true;
	}

	@Override
	protected void defineSynchedData() {
		getEntityData().define(lifetime, 160);
	}

	public void shootFromRotation(Entity shooter, float x, float y, float z, float velocity, float inaccuracy) {
		this.setOwner(shooter);
		super.shoot(x, y, z, velocity, inaccuracy);
	}

	public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
		super.shoot(x, y, z, velocity, inaccuracy);
	}

	public void tick() {
		super.tick();
		int lifetime = getEntityData().get(GlimmerProjectileEntity.lifetime);
		getEntityData().set(GlimmerProjectileEntity.lifetime, lifetime - 1);
		if (lifetime <= 0) {
			this.remove(RemovalReason.DISCARDED);
		}

		Vec3 oldPosition = new Vec3(getX(), getY(), getZ());
		Vec3 newPosVector = oldPosition.add(getDeltaMovement());
		BlockHitResult raytraceresult = level().clip(new ClipContext(oldPosition, newPosVector.add(getDeltaMovement().normalize().scale(1.5)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));

		if (raytraceresult != null && raytraceresult.getType() != HitResult.Type.MISS)
			newPosVector = raytraceresult.getLocation();

		move(MoverType.SELF, newPosVector.subtract(oldPosition));

		setDeltaMovement(getDeltaMovement().add(0, -0.05f, 0));

		if (!level().isClientSide() && raytraceresult != null && raytraceresult.getType() == HitResult.Type.BLOCK && !ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
			this.onHit(raytraceresult);
		}

		if (level().isClientSide()) {
			double deltaX = getX() - oldPosition.x;
			double deltaY = getY() - oldPosition.y;
			double deltaZ = getZ() - oldPosition.z;
			for (double i = 0; i < 9; i ++) {
				double coeff = i / 9.0;
				level().addParticle(GLIMMER, oldPosition.x + deltaX * coeff, oldPosition.y + deltaY * coeff, oldPosition.z + deltaZ * coeff, 01.1f*(Misc.random.nextFloat()-0.5f), 01.1f*(Misc.random.nextFloat()-0.5f), 01.1f*(Misc.random.nextFloat()-0.5f));
			}
		}
	}

	public void onHitBlock(BlockHitResult raytraceresult) {
		super.onHitBlock(raytraceresult);
		Direction side = raytraceresult.getDirection();
		BlockPos hitPos = raytraceresult.getBlockPos().relative(side);
		BlockPlaceContext context = new BlockPlaceContext(level(), (getOwner() instanceof Player) ? (Player) getOwner() : null, InteractionHand.MAIN_HAND, ItemStack.EMPTY, raytraceresult);

		if (level().getBlockState(hitPos).canBeReplaced(context)) {
			level().setBlock(hitPos, RegistryManager.GLIMMER.get().getStateForPlacement(context), 11);

			if (level() instanceof ServerLevel serverLevel) {
				serverLevel.sendParticles(GlimmerBlock.GLIMMER, hitPos.getX() + 0.5, hitPos.getY() + 0.5, hitPos.getZ() + 0.5, 1, 0, 0, 0, 0.0);
				serverLevel.sendParticles(GlimmerBlock.EMBER, hitPos.getX() + 0.5, hitPos.getY() + 0.5, hitPos.getZ() + 0.5, 1, 0, 0.001, 0, 0.0);
			}
		} else {
			if (level() instanceof ServerLevel serverLevel) {
				serverLevel.sendParticles(SMOKE, getX(), getY(), getZ(), 6, 0, 0, 0, 0.0);
			}
		}
		this.remove(RemovalReason.DISCARDED);
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return new ClientboundAddEntityPacket(this);
	}
}
