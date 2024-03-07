package com.rekindled.embers.blockentity;

import java.util.List;
import java.util.Random;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.event.EmberEvent;
import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.api.power.IEmberPacketReceiver;
import com.rekindled.embers.api.tile.ISparkable;
import com.rekindled.embers.api.upgrades.UpgradeContext;
import com.rekindled.embers.api.upgrades.UpgradeUtil;
import com.rekindled.embers.datagen.EmbersDamageTypes;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.network.PacketHandler;
import com.rekindled.embers.network.message.MessageBeamCannonFX;
import com.rekindled.embers.power.DefaultEmberCapability;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;

public class BeamCannonBlockEntity extends BlockEntity {

	public IEmberCapability capability = new DefaultEmberCapability() {
		@Override
		public void onContentsChanged() {
			super.onContentsChanged();
			BeamCannonBlockEntity.this.setChanged();
		}

		@Override
		public boolean acceptsVolatile() {
			return false;
		}
	};
	public static final double PULL_RATE = 2000.0;
	public static final int FIRE_THRESHOLD = 400;
	public static final float DAMAGE = 25.0f;
	public static final int MAX_DISTANCE = 64;

	public long ticksExisted = 0;
	public boolean lastPowered = false;
	public Random random = new Random();
	public int offset = random.nextInt(40);
	protected List<UpgradeContext> upgrades;

	public BeamCannonBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.BEAM_CANNON_ENTITY.get(), pPos, pBlockState);
		capability.setEmberCapacity(2000);
	}

	public BeamCannonBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
		super(pType, pPos, pBlockState);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		lastPowered = nbt.getBoolean("lastPowered");
		capability.deserializeNBT(nbt);
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		nbt.putBoolean("lastPowered", lastPowered);
		capability.writeToNBT(nbt);
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag nbt = super.getUpdateTag();
		saveAdditional(nbt);
		return nbt;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, BeamCannonBlockEntity blockEntity) {
		blockEntity.ticksExisted ++;
		Direction facing = state.getValue(BlockStateProperties.FACING);
		BlockEntity attachedTile = level.getBlockEntity(pos.relative(facing, -1));
		if (blockEntity.ticksExisted % 5 == 0 && attachedTile != null) {
			IEmberCapability cap = attachedTile.getCapability(EmbersCapabilities.EMBER_CAPABILITY, facing).orElse(null);
			if (cap != null) {
				if (cap.getEmber() > 0 && blockEntity.capability.getEmber() < blockEntity.capability.getEmberCapacity()){
					double removed = cap.removeAmount(PULL_RATE, true);
					blockEntity.capability.addAmount(removed, true);
				}
			}
		}
		blockEntity.upgrades = UpgradeUtil.getUpgrades(level, pos, Direction.values());
		UpgradeUtil.verifyUpgrades(blockEntity, blockEntity.upgrades);
		boolean cancel = UpgradeUtil.doWork(blockEntity, blockEntity.upgrades);
		boolean isPowered = level.hasNeighborSignal(pos);
		boolean redstoneEnabled = UpgradeUtil.getOtherParameter(blockEntity, "redstone_enabled", true, blockEntity.upgrades);
		int threshold = UpgradeUtil.getOtherParameter(blockEntity, "fire_threshold", FIRE_THRESHOLD, blockEntity.upgrades);
		if (!cancel && blockEntity.capability.getEmber() >= threshold && (!redstoneEnabled || (isPowered && !blockEntity.lastPowered))){
			blockEntity.fire(facing);
		}
		blockEntity.lastPowered = isPowered;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (!this.remove && cap == EmbersCapabilities.EMBER_CAPABILITY && level.getBlockState(this.getBlockPos()).getValue(BlockStateProperties.FACING) != side) {
			return capability.getCapability(cap, side);
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		capability.invalidate();
	}

	public void fire(Direction facing) {
		Vec3 ray = new Vec3(facing.getNormal().getX(), facing.getNormal().getY(), facing.getNormal().getZ());
		double damage = UpgradeUtil.getOtherParameter(this, "damage", DAMAGE, upgrades);
		boolean doContinue = true;
		int maxDist = UpgradeUtil.getOtherParameter(this, "distance", MAX_DISTANCE, upgrades);
		double impactDist = maxDist;
		BlockPos hitPos = worldPosition;
		for (int i = 0; i < maxDist && doContinue; i++) {
			hitPos = hitPos.relative(facing);
			BlockState state = level.getBlockState(hitPos);
			BlockEntity tile = level.getBlockEntity(hitPos);
			if (sparkTarget(tile)) {
				doContinue = false;
				impactDist = i + 1;
			} else if (tile instanceof IEmberPacketReceiver) {
				IEmberCapability cap = tile.getCapability(EmbersCapabilities.EMBER_CAPABILITY, null).orElseGet(null);
				if (cap != null) {
					cap.addAmount(capability.getEmber(), true);
				}
				doContinue = false;
				impactDist = i + 1;
			} else if (!state.getCollisionShape(level, hitPos).isEmpty()) {
				doContinue = false;
				impactDist = i + 0.5;
			}
			if (!doContinue) {
				level.playSound(null, hitPos, EmbersSounds.BEAM_CANNON_HIT.get(), SoundSource.BLOCKS, 0.5f, 1.0f);
			}
		}
		List<Entity> entities = level.getEntities((Entity) null, new AABB(worldPosition.getCenter(), hitPos.getCenter()), EntitySelector.NO_SPECTATORS);
		for (Entity entity : entities) {
			DamageSource damageSource = new DamageSource(level.registryAccess().registry(Registries.DAMAGE_TYPE).get().getHolderOrThrow(EmbersDamageTypes.EMBER_KEY), worldPosition.getCenter());
			entity.hurt(damageSource, (float)damage);
		}

		PacketHandler.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new MessageBeamCannonFX(worldPosition.getX()+0.5,worldPosition.getY()+0.5,worldPosition.getZ()+0.5,ray.x*impactDist,ray.y*impactDist,ray.z*impactDist,0XFF4010));

		UpgradeUtil.throwEvent(this, new EmberEvent(this, EmberEvent.EnumType.TRANSFER, this.capability.getEmber()), upgrades);
		this.capability.setEmber(0);
		this.setChanged();

		level.playSound(null, worldPosition, EmbersSounds.BEAM_CANNON_FIRE.get(), SoundSource.BLOCKS, 0.7f, 1.0f);
	}

	public boolean sparkTarget(BlockEntity target) {
		if (target instanceof ISparkable) {
			((ISparkable) target).sparkProgress(this, capability.getEmber());
			return true;
		}
		return false;
	}
}
