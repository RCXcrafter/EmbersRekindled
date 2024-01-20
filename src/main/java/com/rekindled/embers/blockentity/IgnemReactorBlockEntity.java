package com.rekindled.embers.blockentity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.joml.Vector3f;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.event.DialInformationEvent;
import com.rekindled.embers.api.event.EmberEvent;
import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.api.tile.IExtraCapabilityInformation;
import com.rekindled.embers.api.tile.IExtraDialInformation;
import com.rekindled.embers.api.upgrades.UpgradeContext;
import com.rekindled.embers.api.upgrades.UpgradeUtil;
import com.rekindled.embers.block.EmberDialBlock;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.particle.GlowParticleOptions;
import com.rekindled.embers.particle.SmokeParticleOptions;
import com.rekindled.embers.power.DefaultEmberCapability;
import com.rekindled.embers.recipe.IEmberActivationRecipe;
import com.rekindled.embers.recipe.SingleItemContainer;
import com.rekindled.embers.util.DecimalFormats;
import com.rekindled.embers.util.Misc;
import com.rekindled.embers.util.sound.ISoundController;

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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class IgnemReactorBlockEntity extends BlockEntity implements ISoundController, IExtraDialInformation, IExtraCapabilityInformation {

	public static final double BASE_MULTIPLIER = 1.0;
	public static final int PROCESS_TIME = 20;
	static Random random = new Random();
	public IEmberCapability capability = new DefaultEmberCapability() {
		@Override
		public void onContentsChanged() {
			super.onContentsChanged();
			IgnemReactorBlockEntity.this.setChanged();
		}

		@Override
		public boolean acceptsVolatile() {
			return true;
		}
	};
	public ItemStackHandler inventory = new ItemStackHandler(1) {
		@Override
		protected void onContentsChanged(int slot) {
			IgnemReactorBlockEntity.this.setChanged();
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (Misc.getRecipe(cachedRecipe, RegistryManager.EMBER_ACTIVATION.get(), new SingleItemContainer(stack), level) != null) {
				return super.insertItem(slot, stack, simulate);
			}
			return stack;
		}
	};
	int progress = -1;

	public LazyOptional<IItemHandler> holder = LazyOptional.of(() -> inventory);
	protected List<UpgradeContext> upgrades = new ArrayList<>();
	public IEmberActivationRecipe cachedRecipe = null;
	public double catalyzerMult;
	public double combustorMult;

	public static final int SOUND_HAS_EMBER = 1;
	public static final int[] SOUND_IDS = new int[]{SOUND_HAS_EMBER};

	HashSet<Integer> soundsPlaying = new HashSet<>();

	public IgnemReactorBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.IGNEM_REACTOR_ENTITY.get(), pPos, pBlockState);
		capability.setEmberCapacity(128000);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		inventory.deserializeNBT(nbt.getCompound("inventory"));
		capability.deserializeNBT(nbt);
		progress = nbt.getInt("progress");
		catalyzerMult = nbt.getDouble("catalyzer");
		combustorMult = nbt.getDouble("combustor");
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		nbt.put("inventory", inventory.serializeNBT());
		capability.writeToNBT(nbt);
		nbt.putInt("progress", progress);
		nbt.putDouble("catalyzer", catalyzerMult);
		nbt.putDouble("combustor", combustorMult);
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

	public static void clientTick(Level level, BlockPos pos, BlockState state, IgnemReactorBlockEntity blockEntity) {
		blockEntity.upgrades = UpgradeUtil.getUpgrades(level, pos, Misc.horizontals);
		UpgradeUtil.verifyUpgrades(blockEntity, blockEntity.upgrades);
		blockEntity.handleSound();
		if (blockEntity.capability.getEmber() > 0) {
			double catalyzerRatio = 0.0;
			if (blockEntity.catalyzerMult > 0 || blockEntity.combustorMult > 0)
				catalyzerRatio = blockEntity.catalyzerMult / (blockEntity.catalyzerMult + blockEntity.combustorMult);
			int r = (int) Mth.clampedLerp(255, 255, catalyzerRatio);
			int g = (int) Mth.clampedLerp(64, 64, catalyzerRatio);
			int b = (int) Mth.clampedLerp(16, 64, catalyzerRatio);
			float size = (float) Mth.clampedLerp(4.0, 2.0, catalyzerRatio);
			GlowParticleOptions options = new GlowParticleOptions(new Vector3f(r / 255f, g / 255f, b / 255f), size);
			for (int i = 0; i < Math.ceil(blockEntity.capability.getEmber() / 500.0); i ++) {
				float vx = (float) Mth.clampedLerp(0, (random.nextFloat() - 0.5) * 0.1f, catalyzerRatio);
				float vy = (float) Mth.clampedLerp(random.nextFloat() * 0.05f, (random.nextFloat() - 0.5) * 0.2f, catalyzerRatio);
				float vz = (float) Mth.clampedLerp(0, (random.nextFloat() - 0.5) * 0.1f, catalyzerRatio);
				level.addParticle(options, pos.getX()+0.25f+random.nextFloat()*0.5f, pos.getY()+0.25f+random.nextFloat()*0.5f, pos.getZ()+0.25f+random.nextFloat()*0.5f, vx, vy, vz);
			}
		}
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, IgnemReactorBlockEntity blockEntity) {
		blockEntity.upgrades = UpgradeUtil.getUpgrades(level, pos, Misc.horizontals);
		UpgradeUtil.verifyUpgrades(blockEntity, blockEntity.upgrades);
		if (UpgradeUtil.doTick(blockEntity, blockEntity.upgrades))
			return;

		boolean cancel = UpgradeUtil.doWork(blockEntity, blockEntity.upgrades);
		if (!cancel && !blockEntity.inventory.getStackInSlot(0).isEmpty()) {
			blockEntity.progress++;
			if (blockEntity.progress > UpgradeUtil.getWorkTime(blockEntity, PROCESS_TIME, blockEntity.upgrades)) {
				blockEntity.catalyzerMult = 0.0;
				blockEntity.combustorMult = 0.0;
				double multiplier = BASE_MULTIPLIER;
				for (Direction facing : Misc.horizontals) {
					BlockEntity tile = level.getBlockEntity(pos.relative(facing).below());
					if (tile instanceof CatalysisChamberBlockEntity)
						blockEntity.catalyzerMult += ((CatalysisChamberBlockEntity) tile).multiplier;
					if (tile instanceof CombustionChamberBlockEntity)
						blockEntity.combustorMult += ((CombustionChamberBlockEntity) tile).multiplier;
				}
				if (Math.max(blockEntity.combustorMult, blockEntity.catalyzerMult) < 2.0f * Math.min(blockEntity.combustorMult, blockEntity.catalyzerMult)) {
					multiplier += blockEntity.combustorMult;
					multiplier += blockEntity.catalyzerMult;
					blockEntity.progress = 0;
					if (blockEntity.inventory != null) {
						RecipeWrapper wrapper = new RecipeWrapper(blockEntity.inventory);
						blockEntity.cachedRecipe = Misc.getRecipe(blockEntity.cachedRecipe, RegistryManager.EMBER_ACTIVATION.get(), wrapper, level);
						if (blockEntity.cachedRecipe != null) {
							double emberValue = blockEntity.cachedRecipe.getOutput(wrapper);
							double ember = UpgradeUtil.getTotalEmberProduction(blockEntity, multiplier * emberValue, blockEntity.upgrades);
							if (ember > 0 && blockEntity.capability.getEmber() + ember <= blockEntity.capability.getEmberCapacity()) {
								level.playSound(null, pos, EmbersSounds.IGNEM_REACTOR.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
								if (level instanceof ServerLevel serverLevel) {
									serverLevel.sendParticles(new GlowParticleOptions(GlowParticleOptions.EMBER_COLOR, new Vec3(0, 0.65f, 0), 4.7f), pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, 80, 0.1, 0.1, 0.1, 1.0);
									serverLevel.sendParticles(new SmokeParticleOptions(SmokeParticleOptions.SMOKE_COLOR, 5.0f), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 20, 0.1, 0.1, 0.1, 1.0);
								}
								UpgradeUtil.throwEvent(blockEntity, new EmberEvent(blockEntity, EmberEvent.EnumType.PRODUCE, ember), blockEntity.upgrades);
								blockEntity.capability.addAmount(ember, true);

								//the recipe is responsible for taking items from the inventory
								blockEntity.cachedRecipe.process(wrapper);
							}
						}
					}
				}
			}
			blockEntity.setChanged();
		}
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (!this.remove) {
			if (cap == ForgeCapabilities.ITEM_HANDLER) {
				return ForgeCapabilities.ITEM_HANDLER.orEmpty(cap, holder);
			}
			if (cap == EmbersCapabilities.EMBER_CAPABILITY) {
				return capability.getCapability(cap, side);
			}
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		holder.invalidate();
		capability.invalidate();
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
	public void playSound(int id) {
		switch (id) {
		case SOUND_HAS_EMBER:
			EmbersSounds.playMachineSound(this, SOUND_HAS_EMBER, EmbersSounds.GENERATOR_LOOP.get(), SoundSource.BLOCKS, true, 1.0f, 1.0f, (float)worldPosition.getX()+0.5f,(float)worldPosition.getY()+0.5f,(float)worldPosition.getZ()+0.5f);
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
		return id == SOUND_HAS_EMBER && capability.getEmber() > 0;
	}

	public float getCurrentVolume(int id, float volume) {
		return (float) ((capability.getEmber() + 5000.0f) / (capability.getEmberCapacity() + 5000.0f));
	}

	@Override
	public boolean hasCapabilityDescription(Capability<?> capability) {
		return capability == ForgeCapabilities.ITEM_HANDLER || capability == EmbersCapabilities.EMBER_CAPABILITY;
	}

	@Override
	public void addCapabilityDescription(List<String> strings, Capability<?> capability, Direction facing) {
		if (capability == ForgeCapabilities.ITEM_HANDLER)
			strings.add(IExtraCapabilityInformation.formatCapability(EnumIOType.INPUT, Embers.MODID + ".tooltip.goggles.item",I18n.get(Embers.MODID + ".tooltip.goggles.item.ember")));
		if (capability == EmbersCapabilities.EMBER_CAPABILITY)
			strings.add(IExtraCapabilityInformation.formatCapability(EnumIOType.OUTPUT, Embers.MODID + ".tooltip.goggles.ember", null));
	}

	@Override
	public void addDialInformation(Direction facing, List<String> information, String dialType) {
		if (EmberDialBlock.DIAL_TYPE.equals(dialType) && Math.max(combustorMult, catalyzerMult) < 2.0f * Math.min(combustorMult, catalyzerMult)) {
			DecimalFormat multiplierFormat = DecimalFormats.getDecimalFormat(Embers.MODID + ".decimal_format.ember_multiplier");
			double multiplier = BASE_MULTIPLIER + combustorMult + catalyzerMult;
			information.add(I18n.get(Embers.MODID + ".tooltip.dial.ember_multiplier", multiplierFormat.format(multiplier)));
		}
		UpgradeUtil.throwEvent(this, new DialInformationEvent(this, information, dialType), upgrades);
	}
}
