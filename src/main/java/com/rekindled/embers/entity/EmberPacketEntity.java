package com.rekindled.embers.entity;

import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.api.power.IEmberPacketReceiver;
import com.rekindled.embers.particle.GlowParticleOptions;
import com.rekindled.embers.particle.StarParticleOptions;
import com.rekindled.embers.util.EmbersColors;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class EmberPacketEntity extends Entity {

	public BlockPos pos = new BlockPos(0,0,0);
	public BlockPos dest = new BlockPos(0,0,0);
	public double value = 0;
	public static final EntityDataAccessor<Integer> lifetime = SynchedEntityData.defineId(EmberProjectileEntity.class, EntityDataSerializers.INT);

	public EmberPacketEntity(EntityType<?> pEntityType, Level pLevel) {
		super(pEntityType, pLevel);
		this.setNoGravity(true);
		this.setInvulnerable(true);
		this.noPhysics = true;
	}

	public void initCustom(BlockPos pos, BlockPos dest, double vx, double vy, double vz, double value) {
		this.moveTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
		this.setDeltaMovement(vx, vy, vz);
		this.dest = dest;
		this.pos = pos;
		this.value = value;
	}

	public void setLifetime(int time) {
		getEntityData().set(lifetime, time);
	}

	@Override
	protected void defineSynchedData() {
		getEntityData().define(lifetime, 80);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag nbt) {
		if (nbt.contains("destX")){
			dest = new BlockPos(nbt.getInt("destX"), nbt.getInt("destY"), nbt.getInt("destZ"));
		}
		value = nbt.getDouble("value");
		getEntityData().set(lifetime, nbt.getInt("lifetime"));
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag nbt) {
		if (dest != null){
			nbt.putInt("destX", dest.getX());
			nbt.putInt("destY", dest.getY());
			nbt.putInt("destZ", dest.getZ());
		}
		nbt.putDouble("value", value);
		nbt.putInt("lifetime", getEntityData().get(lifetime));
	}

	public void tick() {
		//super.tick();
		int lifetime = getEntityData().get(EmberPacketEntity.lifetime);
		getEntityData().set(EmberPacketEntity.lifetime, lifetime - 1);
		lifetime --;
		if (lifetime <= 0) {
			this.remove(RemovalReason.DISCARDED);
		}
		if (!this.isRemoved()) {
			Vec3 oldPosition = new Vec3(getX(), getY(), getZ());
			if (dest.getX() != 0 || dest.getY() != 0 || dest.getZ() != 0){
				double targetX = dest.getX() + 0.5;
				double targetY = dest.getY() + 0.5;
				double targetZ = dest.getZ() + 0.5;
				Vec3 targetVector = new Vec3(targetX - getX(), targetY - getY(), targetZ - getZ());
				double length = targetVector.length();
				targetVector = targetVector.scale(0.3 / length);
				double weight = 0;
				if (length <= 3) {
					weight = 0.9 * ((3.0 - length) / 3.0);
				}
				setDeltaMovement(
						(0.9 - weight) * getDeltaMovement().x + (0.1 + weight) * targetVector.x,
						(0.9 - weight) * getDeltaMovement().y + (0.1 + weight) * targetVector.y,
						(0.9 - weight) * getDeltaMovement().z + (0.1 + weight) * targetVector.z);
			}
			move(MoverType.SELF, getDeltaMovement());

			BlockPos pos = blockPosition();
			if (getX() > pos.getX()+0.25 && getX() < pos.getX()+0.75 && getY() > pos.getY()+0.25 && this.getY() < pos.getY()+0.75 && getZ() > pos.getZ()+0.25 && getZ() < pos.getZ()+0.75) {
				affectTileEntity(level().getBlockState(blockPosition()), level().getBlockEntity(blockPosition()));
			}
			if (level().isClientSide() && lifetime != 80) {
				if (lifetime == 79) {
					for (double i = 0; i < 12; i ++) {
						level().addParticle(new StarParticleOptions(EmbersColors.EMBER_ID, 3.5f + 0.5f * random.nextFloat()), getX(), getY(), getZ(), 0.125f*(random.nextFloat()-0.5f), 0.125f*(random.nextFloat()-0.5f), 0.125f*(random.nextFloat()-0.5f));
					}
				}
				double deltaX = getX() - oldPosition.x;
				double deltaY = getY() - oldPosition.y;
				double deltaZ = getZ() - oldPosition.z;
				double dist = Math.ceil(Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 20);
				for (double i = 0; i < dist; i ++) {
					double coeff = i / dist;
					level().addParticle(GlowParticleOptions.EMBER, oldPosition.x + deltaX * coeff, oldPosition.y + deltaY * coeff, oldPosition.z + deltaZ * coeff, 0.125f*(random.nextFloat()-0.5f), 0.125f*(random.nextFloat()-0.5f), 0.125f*(random.nextFloat()-0.5f));
				}
			}
		}
	}

	public void affectTileEntity(BlockState state, BlockEntity blockEntity) {
		if (blockEntity instanceof IEmberPacketReceiver && getEntityData().get(lifetime) > 1) {
			if (((IEmberPacketReceiver) blockEntity).onReceive(this)) {
				IEmberCapability capability = blockEntity.getCapability(EmbersCapabilities.EMBER_CAPABILITY).orElse(null);
				if (capability != null) {
					capability.addAmount(value, true);
					blockEntity.setChanged();
				}
				setDeltaMovement(0, 0, 0);
				//stay alive for one more tick for the sake of the particles reaching the receptor properly
				getEntityData().set(lifetime, 2);
			}
		}
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return new ClientboundAddEntityPacket(this);
	}
}
