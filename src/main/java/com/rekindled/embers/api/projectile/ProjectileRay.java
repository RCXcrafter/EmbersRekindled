package com.rekindled.embers.api.projectile;

import java.awt.Color;
import java.util.List;

import javax.annotation.Nullable;

import com.rekindled.embers.network.PacketHandler;
import com.rekindled.embers.network.message.MessageEmberRayFX;
import com.rekindled.embers.util.Misc;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

public class ProjectileRay implements IProjectilePreset {

	Vec3 pos;
	Vec3 velocity;
	IProjectileEffect effect;
	Entity shooter;
	boolean pierceEntities;
	Color color = new Color(255,64,16);

	public ProjectileRay(Entity shooter, Vec3 start, Vec3 end, boolean pierceEntities, IProjectileEffect effect) {
		this.pos = start;
		this.velocity = end.subtract(start);
		this.effect = effect;
		this.shooter = shooter;
		this.pierceEntities = pierceEntities;
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
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public IProjectileEffect getEffect() {
		return effect;
	}

	@Override
	public void setEffect(IProjectileEffect effect) {
		this.effect = effect;
	}

	public boolean canPierceEntities() {
		return pierceEntities;
	}

	public void setPierceEntities(boolean pierceEntities) {
		this.pierceEntities = pierceEntities;
	}

	@Nullable
	@Override
	public Entity getEntity() {
		return null;
	}

	@Nullable
	@Override
	public Entity getShooter() {
		return shooter;
	}

	@Override
	public void shoot(Level world) {
		double startX = getPos().x;
		double startY = getPos().y;
		double startZ = getPos().z;

		double dX = getVelocity().x;
		double dY = getVelocity().y;
		double dZ = getVelocity().z;

		double impactDist = Double.POSITIVE_INFINITY;
		boolean doContinue = true;
		Vec3 currPosVec = getPos();
		Vec3 newPosVector = getPos().add(getVelocity());
		BlockHitResult blockTrace = world.clip(new ClipContext(currPosVec, newPosVector, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, shooter));
		List<EntityHitResult> entityTraces = Misc.getEntityHitResults(world, null, shooter, currPosVec, newPosVector, new AABB(startX-0.3,startY-0.3,startZ-0.3,startX+0.3,startY+0.3,startZ+0.3), EntitySelector.NO_SPECTATORS, 0.3f);

		double distBlock = blockTrace != null ? getPos().distanceToSqr(blockTrace.getLocation()) : Double.POSITIVE_INFINITY;

		for (HitResult entityTraceFirst : entityTraces) {
			if (doContinue && entityTraceFirst != null && getPos().distanceToSqr(entityTraceFirst.getLocation()) < distBlock) {
				effect.onHit(world, entityTraceFirst, this);
				if (!pierceEntities) {
					impactDist = getPos().distanceTo(entityTraceFirst.getLocation());
					doContinue = false;
				}
			}
		}

		if (doContinue && blockTrace != null) {
			effect.onHit(world,blockTrace,this);
			impactDist = getPos().distanceTo(blockTrace.getLocation());
			doContinue = false;
		}

		if (doContinue) {
			effect.onFizzle(world,newPosVector,this);
			impactDist = getPos().distanceTo(newPosVector);
		}

		if (!world.isClientSide) {
			PacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> shooter), new MessageEmberRayFX(startX,startY,startZ,dX,dY,dZ,impactDist,Misc.intColor(color.getRed(), color.getGreen(), color.getBlue())));
		}
	}
}
