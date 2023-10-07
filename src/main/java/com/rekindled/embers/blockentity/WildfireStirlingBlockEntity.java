package com.rekindled.embers.blockentity;

import java.util.List;
import java.util.Random;

import org.joml.Vector3f;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.tile.IExtraCapabilityInformation;
import com.rekindled.embers.particle.VaporParticleOptions;
import com.rekindled.embers.recipe.GaseousFuelRecipe;
import com.rekindled.embers.upgrade.UpgradeWildfireStirling;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class WildfireStirlingBlockEntity extends BlockEntity implements /*ISoundController, */ IExtraCapabilityInformation {

	/*public static final int SOUND_OFF = 1;
	public static final int SOUND_ON = 2;
	public static final int[] SOUND_IDS = new int[]{SOUND_OFF,SOUND_ON};*/

	public int activeTicks = 0;
	public int burnTime = 0;
	public UpgradeWildfireStirling upgrade;
	public FluidTank tank = new FluidTank(FluidType.BUCKET_VOLUME * 4) {
		@Override
		public void onContentsChanged() {
			WildfireStirlingBlockEntity.this.setChanged();
		}
	};
	private static Random random = new Random();
	public GaseousFuelRecipe cachedRecipe = null;

	//HashSet<Integer> soundsPlaying = new HashSet<>();
	public LazyOptional<IFluidHandler> holder = LazyOptional.of(() -> tank);

	public WildfireStirlingBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.WILDFIRE_STIRLING_ENTITY.get(), pPos, pBlockState);
		upgrade = new UpgradeWildfireStirling(this);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		tank.readFromNBT(nbt.getCompound("tank"));
		activeTicks = nbt.getInt("active");
		burnTime = nbt.getInt("burnTime");
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		nbt.put("tank", tank.writeToNBT(new CompoundTag()));
		nbt.putInt("active", activeTicks);
		nbt.putInt("burnTime", burnTime);
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

	public void setActive(int ticks) {
		activeTicks = Math.max(ticks, activeTicks);
		setChanged();
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, WildfireStirlingBlockEntity blockEntity) {
		blockEntity.activeTicks--;
	}

	public static void clientTick(Level level, BlockPos pos, BlockState state, WildfireStirlingBlockEntity blockEntity) {
		//blockEntity.handleSound();
		blockEntity.activeTicks--;

		if (blockEntity.activeTicks > 0 && state.hasProperty(BlockStateProperties.FACING)) {
			Direction facing = state.getValue(BlockStateProperties.FACING).getOpposite();
			float frontoffset = -0.6f;
			float yoffset = 0.2f;
			float wideoffset = 0.5f;
			float breadthoffset = 0.4f;
			Vec3 frontOffset = new Vec3(0.5 - facing.getNormal().getX() * frontoffset, 0.5 - facing.getNormal().getY() * frontoffset, 0.5 - facing.getNormal().getZ() * frontoffset);
			Vec3 baseOffset = new Vec3(0.5 - facing.getNormal().getX() * yoffset, 0.5 - facing.getNormal().getY() * yoffset, 0.5 - facing.getNormal().getZ() * yoffset);
			Direction[] planars;
			switch (facing.getAxis()) {
			case X:
				planars = new Direction[] {Direction.DOWN,Direction.UP,Direction.NORTH,Direction.SOUTH}; break;
			case Y:
				planars = new Direction[] {Direction.EAST,Direction.WEST,Direction.NORTH,Direction.SOUTH}; break;
			case Z:
				planars = new Direction[] {Direction.DOWN,Direction.UP,Direction.EAST,Direction.WEST}; break;
			default:
				planars = null; break;
			}
			Vector3f color = new Vector3f(255.0F / 255.0F, 64.0F / 255.0F, 16.0F / 255.0F);
			for (Direction planar : planars) {
				BlockState sideState = level.getBlockState(pos.relative(planar));
				if (!sideState.getFaceOcclusionShape(level, pos.relative(planar), planar.getOpposite()).isEmpty())
					continue;
				Direction cross = facing.getClockWise(planar.getAxis());
				float x1 = pos.getX() + (float) baseOffset.x + planar.getNormal().getX() * wideoffset;
				float y1 = pos.getY() + (float) baseOffset.y + planar.getNormal().getY() * wideoffset;
				float z1 = pos.getZ() + (float) baseOffset.z + planar.getNormal().getZ() * wideoffset;
				float x2 = pos.getX() + (float) frontOffset.x + planar.getNormal().getX() * wideoffset + cross.getNormal().getX() * (random.nextFloat()-0.5f) * 2 * breadthoffset;
				float y2 = pos.getY() + (float) frontOffset.y + planar.getNormal().getY() * wideoffset + cross.getNormal().getY() * (random.nextFloat()-0.5f) * 2 * breadthoffset;
				float z2 = pos.getZ() + (float) frontOffset.z + planar.getNormal().getZ() * wideoffset + cross.getNormal().getZ() * (random.nextFloat()-0.5f) * 2 * breadthoffset;
				int lifetime = 24 + random.nextInt(8);
				//float motionx = facing.getNormal().getX() * (1.0f/lifetime) - 0.01f + random.nextFloat() * 0.02f;
				//float motiony = facing.getNormal().getY() * (1.0f/lifetime) - 0.01f + random.nextFloat() * 0.02f;
				//float motionz = facing.getNormal().getZ() * (1.0f/lifetime) - 0.01f + random.nextFloat() * 0.02f;
				float motionx = (x2 - x1) / lifetime;
				float motiony = (y2 - y1) / lifetime;
				float motionz = (z2 - z1) / lifetime;

				level.addParticle(new VaporParticleOptions(color, new Vec3(motionx, motiony, motionz), lifetime / 16.0f), x1, y1, z1, 0, 0, 0);
			}
			float x = pos.getX() + (float) frontOffset.x;
			float y = pos.getY() + (float) frontOffset.y;
			float z = pos.getZ() + (float) frontOffset.z;
			int lifetime = 16 + random.nextInt(16);
			float motionx = (Math.abs(facing.getNormal().getX()) - 1) * (random.nextFloat()-0.5f) * 2 * wideoffset / lifetime;
			float motiony = (Math.abs(facing.getNormal().getY()) - 1) * (random.nextFloat()-0.5f) * 2 * wideoffset / lifetime;
			float motionz = (Math.abs(facing.getNormal().getZ()) - 1) * (random.nextFloat()-0.5f) * 2 * wideoffset / lifetime;

			level.addParticle(new VaporParticleOptions(color, new Vec3(motionx, motiony, motionz), lifetime / 16.0f), x, y, z, 0, 0, 0);
		}
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (!this.remove && level.getBlockState(worldPosition).hasProperty(BlockStateProperties.FACING)) {
			Direction facing = level.getBlockState(worldPosition).getValue(BlockStateProperties.FACING);
			if (cap == ForgeCapabilities.FLUID_HANDLER && (side == null || side == facing)) {
				return ForgeCapabilities.FLUID_HANDLER.orEmpty(cap, holder);
			}
			if (cap == EmbersCapabilities.UPGRADE_PROVIDER_CAPABILITY && (side == null || side.getOpposite() == facing)) {
				return upgrade.getCapability(cap, side);
			}
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		holder.invalidate();
		upgrade.invalidate();
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (level instanceof ServerLevel serverLevel) {
			for (ServerPlayer serverplayer : serverLevel.getServer().getPlayerList().getPlayers()) {
				serverplayer.connection.send(this.getUpdatePacket());
			}
		}
	}

	/*@Override
	public void playSound(int id) {
		float soundX = (float) worldPosition.getX() + 0.5f;
		float soundY = (float) worldPosition.getY() + 0.5f;
		float soundZ = (float) worldPosition.getZ() + 0.5f;
		switch (id) {
		case SOUND_ON:
			EmbersSounds.playMachineSound(this, SOUND_ON, EmbersSounds.CATALYTIC_PLUG_LOOP.get(), SoundSource.BLOCKS, true, 1.0f, 1.0f, soundX, soundY, soundZ);
			level.playSound(null, worldPosition, EmbersSounds.CATALYTIC_PLUG_START.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
			break;
		case SOUND_OFF:
			EmbersSounds.playMachineSound(this, SOUND_OFF, EmbersSounds.CATALYTIC_PLUG_LOOP_READY.get(), SoundSource.BLOCKS, true, 1.0f, 1.0f, soundX, soundY, soundZ);
			break;
		}
		soundsPlaying.add(id);
	}

	@Override
	public void stopSound(int id) {
		if(id == SOUND_ON) {
			level.playSound(null, worldPosition, EmbersSounds.CATALYTIC_PLUG_STOP.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
		}
		soundsPlaying.remove(id);
	}

	@Override
	public boolean isSoundPlaying(int id) {
		return soundsPlaying.contains(id);
	}

	@Override
	public int[] getSoundIDs() {
		return SOUND_IDS;
	}

	@Override
	public boolean shouldPlaySound(int id) {
		boolean isWorking = activeTicks > 0;

		switch (id)
		{
		case SOUND_OFF: return !isWorking && tank.getFluidAmount() > 0;
		case SOUND_ON: return isWorking;
		default: return false;
		}
	}

	@Override
	public float getCurrentVolume(int id, float volume) {
		boolean isWorking = activeTicks > 0;

		switch (id)
		{
		case SOUND_OFF: return !isWorking ? 1.0f : 0.0f;
		case SOUND_ON: return isWorking ? 1.0f : 0.0f;
		default: return 0f;
		}
	}*/

	@Override
	public boolean hasCapabilityDescription(Capability<?> capability) {
		return capability == ForgeCapabilities.FLUID_HANDLER;
	}

	@Override
	public void addCapabilityDescription(List<String> strings, Capability<?> capability, Direction facing) {
		if (capability == ForgeCapabilities.FLUID_HANDLER)
			strings.add(IExtraCapabilityInformation.formatCapability(EnumIOType.INPUT, Embers.MODID + ".tooltip.goggles.fluid", I18n.get(Embers.MODID + ".tooltip.goggles.fluid.steam")));
	}
}
