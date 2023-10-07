package com.rekindled.embers.blockentity;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.joml.Vector3f;

import com.rekindled.embers.ConfigManager;
import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.projectile.EffectDamage;
import com.rekindled.embers.api.tile.IExtraCapabilityInformation;
import com.rekindled.embers.api.tile.IExtraDialInformation;
import com.rekindled.embers.block.FluidDialBlock;
import com.rekindled.embers.datagen.EmbersBlockTags;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.entity.EmberProjectileEntity;
import com.rekindled.embers.particle.VaporParticleOptions;
import com.rekindled.embers.recipe.FluidHandlerContext;
import com.rekindled.embers.recipe.BoilingRecipe;
import com.rekindled.embers.upgrade.MiniBoilerUpgrade;
import com.rekindled.embers.util.Misc;
import com.rekindled.embers.util.sound.ISoundController;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class MiniBoilerBlockEntity extends PipeBlockEntityBase implements ISoundController, IExtraDialInformation, IExtraCapabilityInformation {

	public static final int SOUND_SLOW = 1;
	public static final int SOUND_MEDIUM = 2;
	public static final int SOUND_FAST = 3;
	public static final int SOUND_PRESSURE_LOW = 4;
	public static final int SOUND_PRESSURE_MEDIUM = 5;
	public static final int SOUND_PRESSURE_HIGH = 6;
	public static final int[] SOUND_IDS = new int[]{SOUND_SLOW, SOUND_MEDIUM, SOUND_FAST, SOUND_PRESSURE_LOW, SOUND_PRESSURE_MEDIUM, SOUND_PRESSURE_HIGH};

	Random random = new Random();
	HashSet<Integer> soundsPlaying = new HashSet<>();
	protected FluidTank fluidTank = new FluidTank(ConfigManager.MINI_BOILER_CAPACITY.get()) {
		@Override
		public void onContentsChanged() {
			MiniBoilerBlockEntity.this.setChanged();
		}
	};
	public LazyOptional<IFluidHandler> fluidHolder = LazyOptional.of(() -> fluidTank);
	protected FluidTank gasTank = new FluidTank(ConfigManager.MINI_BOILER_CAPACITY.get()) {
		@Override
		public void onContentsChanged() {
			MiniBoilerBlockEntity.this.setChanged();
		}
	};
	public LazyOptional<IFluidHandler> gasHolder = LazyOptional.of(() -> gasTank);
	protected MiniBoilerUpgrade upgrade;
	int lastBoil;
	int boilTime;
	public BoilingRecipe cachedRecipe = null;

	public MiniBoilerBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.MINI_BOILER_ENTITY.get(), pPos, pBlockState);
		upgrade = new MiniBoilerUpgrade(this);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		Direction facing = level.getBlockState(worldPosition).getValue(BlockStateProperties.HORIZONTAL_FACING);
		if (cap == ForgeCapabilities.FLUID_HANDLER) {
			if (side == Direction.UP) {
				return ForgeCapabilities.FLUID_HANDLER.orEmpty(cap, gasHolder);
			}
			if (side == Direction.DOWN || side != facing) {
				return ForgeCapabilities.FLUID_HANDLER.orEmpty(cap, fluidHolder);
			}
		}
		if (cap == EmbersCapabilities.UPGRADE_PROVIDER_CAPABILITY && side == facing) {
			return upgrade.getCapability(cap, side);
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		fluidTank.readFromNBT(nbt.getCompound("fluidTank"));
		gasTank.readFromNBT(nbt.getCompound("gasTank"));
		lastBoil = nbt.getInt("lastBoil");
		boilTime = nbt.getInt("boilTime");
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		nbt.put("fluidTank", fluidTank.writeToNBT(new CompoundTag()));
		nbt.put("gasTank", gasTank.writeToNBT(new CompoundTag()));
		nbt.putInt("lastBoil", lastBoil);
		nbt.putInt("boilTime", boilTime);
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

	public void initConnections() {
		Block block = level.getBlockState(worldPosition).getBlock();
		for (Direction direction : Misc.horizontals) {
			BlockState facingState = level.getBlockState(worldPosition.relative(direction));
			BlockEntity facingBE = level.getBlockEntity(worldPosition.relative(direction));
			if (facingState.is(EmbersBlockTags.FLUID_PIPE_CONNECTION)) {
				if (facingBE instanceof PipeBlockEntityBase && !((PipeBlockEntityBase) facingBE).getConnection(direction.getOpposite()).transfer) {
					connections[direction.get3DDataValue()] = PipeConnection.NONE;
				} else {
					connections[direction.get3DDataValue()] = PipeConnection.PIPE;
				}
			} else {
				connections[direction.get3DDataValue()] = PipeConnection.NONE;
			}
		}
		loaded = true;
		setChanged();
		level.getChunkAt(worldPosition).setUnsaved(true);
		level.updateNeighbourForOutputSignal(worldPosition, block);
	}

	public int getCapacity() {
		return ConfigManager.MINI_BOILER_CAPACITY.get();
	}

	public int getFluidAmount() {
		return fluidTank.getFluidAmount();
	}

	public int getGasAmount() {
		return gasTank.getFluidAmount();
	}

	public FluidTank getFluidTank() {
		return fluidTank;
	}

	public FluidTank getGasTank() {
		return gasTank;
	}

	public Fluid getFluid() {
		if (fluidTank.getFluid() != null){
			return fluidTank.getFluid().getFluid();
		}
		return null;
	}

	public Fluid getGas() {
		if (gasTank.getFluid() != null){
			return gasTank.getFluid().getFluid();
		}
		return null;
	}

	public FluidStack getFluidStack() {
		return fluidTank.getFluid();
	}

	public FluidStack getGasStack() {
		return gasTank.getFluid();
	}

	public void boil(double heat) {
		FluidStack fluid = getFluidStack();
		FluidHandlerContext context = new FluidHandlerContext(fluidTank);
		cachedRecipe = Misc.getRecipe(cachedRecipe, RegistryManager.BOILING.get(), context, level);

		if (cachedRecipe != null && fluid.getAmount() > 0 && heat > 0) {
			int fluidBoiled = Mth.clamp((int) (ConfigManager.MINI_BOILER_HEAT_MULTIPLIER.get() * heat), 1, fluid.getAmount());
			if (fluidBoiled > 0) {
				//the recipe is responsible for draining boiled fluid
				FluidStack gas = cachedRecipe.process(context, fluidBoiled);
				if (gas != null) {
					int leftover = gas.getAmount() - gasTank.fill(gas, FluidAction.EXECUTE);
					if (ConfigManager.MINI_BOILER_CAN_EXPLODE.get() && leftover > 0 && !level.isClientSide()) {
						explode();
					}
				}
			}
			lastBoil = fluidBoiled;
			boilTime = fluidBoiled / 200;
		}
	}

	public void explode() {
		double posX = worldPosition.getX() + 0.5;
		double posY = worldPosition.getY() + 0.5;
		double posZ = worldPosition.getZ() + 0.5;
		level.playSound(null, worldPosition, EmbersSounds.MINI_BOILER_RUPTURE.get(), SoundSource.BLOCKS, 0.6f, 1.0f); //TODO: Random pitch
		Explosion explosion = level.explode(null, posX, posY, posZ, 3f, ExplosionInteraction.NONE);
		level.removeBlock(worldPosition, false);
		EffectDamage effect = new EffectDamage(4.0f, preset -> level.damageSources().explosion(explosion), 10, 0.0f);
		for(int i = 0; i < 12; i++) {
			EmberProjectileEntity proj = RegistryManager.EMBER_PROJECTILE.get().create(level);
			proj.shoot(random.nextDouble()-0.5, random.nextDouble()-0.5, random.nextDouble()-0.5, 0.5f, 0.0f, 10.0f);
			proj.setPos(posX, posY, posZ);
			proj.setLifetime(20 + random.nextInt(40));
			proj.setEffect(effect);
			level.addFreshEntity(proj);
		}
	}

	public static void clientTick(Level level, BlockPos pos, BlockState state, MiniBoilerBlockEntity blockEntity) {
		blockEntity.handleSound();
		blockEntity.spawnParticles();
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, MiniBoilerBlockEntity blockEntity) {
		if (!blockEntity.loaded)
			blockEntity.initConnections();
		if (blockEntity.boilTime > 0) {
			blockEntity.boilTime--;
			blockEntity.setChanged();
		}
	}

	@Override
	public void addDialInformation(Direction facing, List<String> information, String dialType) {
		if (FluidDialBlock.DIAL_TYPE.equals(dialType) && facing.getAxis() != Direction.Axis.Y) {
			String gasFormat = "";
			if(getGasAmount() > getCapacity() * 0.8)
				gasFormat = ChatFormatting.RED.toString() + " ";
			else if(getGasAmount() > getCapacity() * 0.5)
				gasFormat = ChatFormatting.YELLOW.toString() + " ";
			information.add(0, gasFormat + FluidDialBlock.formatFluidStack(getGasStack(), getCapacity()));
		}
	}

	@Override
	public int getComparatorData(Direction facing, int data, String dialType) {
		if (FluidDialBlock.DIAL_TYPE.equals(dialType) && facing.getAxis() != Direction.Axis.Y) {
			double fill = getGasAmount() / (double)getCapacity();
			return fill > 0 ? (int) (1 + fill * 14) : 0;
		}
		return data;
	}

	public void spawnParticles() {
		double gasRatio = getGasAmount() / (double)getCapacity();
		int spouts = 0;
		if(gasRatio > 0.8)
			spouts = 3;
		else if(gasRatio > 0.5)
			spouts = 2;
		else if(gasRatio > 0.25)
			spouts = 1;

		@SuppressWarnings("resource")
		Vector3f color = IClientFluidTypeExtensions.of(getGas().getFluidType()).modifyFogColor(Minecraft.getInstance().gameRenderer.getMainCamera(), 0, (ClientLevel) level, 6, 0, new Vector3f(1, 1, 1));
		Random posRand = new Random(worldPosition.asLong());
		for (int i = 0; i < spouts; i++) {
			double angleA = posRand.nextDouble() * Math.PI * 2;
			double angleB = posRand.nextDouble() * Math.PI * 2;
			float xOffset = (float) (Math.cos(angleA) * Math.cos(angleB));
			float yOffset = (float) (Math.sin(angleA) * Math.cos(angleB));
			float zOffset = (float) Math.sin(angleB);
			float speed = 0.13875f;
			float vx = xOffset * speed + posRand.nextFloat() * speed * 0.3f;
			float vy = yOffset * speed + posRand.nextFloat() * speed * 0.3f;
			float vz = zOffset * speed + posRand.nextFloat() * speed * 0.3f;
			level.addParticle(new VaporParticleOptions(color, new Vec3(vx, vy, vz), 1.0F), worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, 0, 0, 0);
		}
	}

	@Override
	public void playSound(int id) {
		float soundX = (float) worldPosition.getX() + 0.5f;
		float soundY = (float) worldPosition.getY() + 0.5f;
		float soundZ = (float) worldPosition.getZ() + 0.5f;
		switch (id) {
		case SOUND_SLOW:
			EmbersSounds.playMachineSound(this, SOUND_SLOW, EmbersSounds.MINI_BOILER_LOOP_SLOW.get(), SoundSource.BLOCKS, true, 1.0f, 1.0f, soundX, soundY, soundZ);
			break;
		case SOUND_MEDIUM:
			EmbersSounds.playMachineSound(this, SOUND_MEDIUM, EmbersSounds.MINI_BOILER_LOOP_MID.get(), SoundSource.BLOCKS, true, 1.0f, 1.0f, soundX, soundY, soundZ);
			break;
		case SOUND_FAST:
			EmbersSounds.playMachineSound(this, SOUND_FAST, EmbersSounds.MINI_BOILER_LOOP_FAST.get(), SoundSource.BLOCKS, true, 1.0f, 1.0f, soundX, soundY, soundZ);
			break;
		case SOUND_PRESSURE_LOW:
			EmbersSounds.playMachineSound(this, SOUND_PRESSURE_LOW, EmbersSounds.MINI_BOILER_PRESSURE_LOW.get(), SoundSource.BLOCKS, true, 1.0f, 1.0f, soundX, soundY, soundZ);
			break;
		case SOUND_PRESSURE_MEDIUM:
			EmbersSounds.playMachineSound(this, SOUND_PRESSURE_MEDIUM, EmbersSounds.MINI_BOILER_PRESSURE_MID.get(), SoundSource.BLOCKS, true, 1.0f, 1.0f, soundX, soundY, soundZ);
			break;
		case SOUND_PRESSURE_HIGH:
			EmbersSounds.playMachineSound(this, SOUND_PRESSURE_HIGH, EmbersSounds.MINI_BOILER_PRESSURE_HIGH.get(), SoundSource.BLOCKS, true, 1.0f, 1.0f, soundX, soundY, soundZ);
			break;
		}
		soundsPlaying.add(id);
	}

	@Override
	public void stopSound(int id) {
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
		int speedId = 0;
		int pressureId = 0;

		int gasAmount = getGasAmount();
		double gasRatio = gasAmount / (double) getCapacity();
		if (gasRatio > 0.8)
			pressureId = SOUND_PRESSURE_HIGH;
		else if (gasRatio > 0.5)
			pressureId = SOUND_PRESSURE_MEDIUM;
		else if (gasRatio > 0.25)
			pressureId = SOUND_PRESSURE_LOW;

		if (boilTime > 0 && lastBoil > 0) {
			if (lastBoil >= 2400)
				speedId = SOUND_FAST;
			else if (lastBoil >= 400)
				speedId = SOUND_MEDIUM;
			else
				speedId = SOUND_SLOW;
		}

		return speedId == id || pressureId == id;
	}

	@Override
	public float getCurrentPitch(int id, float pitch) {
		if (id == SOUND_PRESSURE_MEDIUM) {
			return 1.0f + (getGasAmount() / (float) getCapacity() - 0.5f) * 1.7f;
		}
		if (id == SOUND_PRESSURE_HIGH) {
			return 1.0f + (getGasAmount() / (float) getCapacity() - 0.8f) * 1.0f;
		}
		return pitch;
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

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		fluidHolder.invalidate();
		gasHolder.invalidate();
		upgrade.invalidate();
	}

	@Override
	public boolean hasCapabilityDescription(Capability<?> capability) {
		return capability == ForgeCapabilities.FLUID_HANDLER;
	}

	@Override
	public void addCapabilityDescription(List<String> strings, Capability<?> capability, Direction facing) {
		if (facing == Direction.UP)
			strings.add(IExtraCapabilityInformation.formatCapability(EnumIOType.OUTPUT, Embers.MODID + ".tooltip.goggles.fluid", I18n.get(Embers.MODID + ".tooltip.goggles.fluid.steam")));
		else if (facing == Direction.DOWN || facing != level.getBlockState(worldPosition).getValue(BlockStateProperties.HORIZONTAL_FACING))
			strings.add(IExtraCapabilityInformation.formatCapability(EnumIOType.INPUT, Embers.MODID + ".tooltip.goggles.fluid", I18n.get(Embers.MODID + ".tooltip.goggles.fluid.water")));
	}
}
