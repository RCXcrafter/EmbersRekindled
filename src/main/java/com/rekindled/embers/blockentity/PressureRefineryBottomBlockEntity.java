package com.rekindled.embers.blockentity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.event.DialInformationEvent;
import com.rekindled.embers.api.event.EmberEvent;
import com.rekindled.embers.api.tile.IExtraCapabilityInformation;
import com.rekindled.embers.api.tile.IExtraDialInformation;
import com.rekindled.embers.api.upgrades.IUpgradeProvider;
import com.rekindled.embers.api.upgrades.UpgradeUtil;
import com.rekindled.embers.block.EmberDialBlock;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.particle.GlowParticleOptions;
import com.rekindled.embers.particle.SmokeParticleOptions;
import com.rekindled.embers.recipe.BlockStateContext;
import com.rekindled.embers.recipe.EmberActivationRecipe;
import com.rekindled.embers.recipe.MetalCoefficientRecipe;
import com.rekindled.embers.recipe.SingleItemContainer;
import com.rekindled.embers.util.DecimalFormats;
import com.rekindled.embers.util.Misc;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.FluidHandlerBlockEntity;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class PressureRefineryBottomBlockEntity extends FluidHandlerBlockEntity implements IExtraDialInformation, IExtraCapabilityInformation {

	public static final float BASE_MULTIPLIER = 1.5f;
	public static final int FLUID_CONSUMED = 25;
	public static final float PER_BLOCK_MULTIPLIER = 0.375f;
	public static final int PROCESS_TIME = 20;
	public static int capacity = FluidType.BUCKET_VOLUME * 8;
	int progress = -1;
	public ItemStackHandler inventory = new ItemStackHandler(1) {
		@Override
		protected void onContentsChanged(int slot) {
			PressureRefineryBottomBlockEntity.this.setChanged();
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (level.getRecipeManager().getRecipesFor(RegistryManager.EMBER_ACTIVATION.get(), new SingleItemContainer(stack), level).isEmpty()) {
				return stack;
			}
			return super.insertItem(slot, stack, simulate);
		}
	};
	public LazyOptional<IItemHandler> holder = LazyOptional.of(() -> inventory);
	private List<IUpgradeProvider> upgrades = new ArrayList<>();
	public EmberActivationRecipe cachedRecipe = null;
	public MetalCoefficientRecipe cachedCoefficient = null;

	public PressureRefineryBottomBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.PRESSURE_REFINERY_BOTTOM_ENTITY.get(), pPos, pBlockState);
		tank = new FluidTank(capacity) {
			@Override
			protected void onContentsChanged() {
				PressureRefineryBottomBlockEntity.this.setChanged();
			}
		};
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		inventory.deserializeNBT(nbt.getCompound("inventory"));
		progress = nbt.getInt("progress");
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		nbt.put("inventory", inventory.serializeNBT());
		nbt.putInt("progress", progress);
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

	public double getMultiplier() {
		BlockState metalState = level.getBlockState(worldPosition.below());
		BlockStateContext context = new BlockStateContext(metalState);
		cachedCoefficient = Misc.getRecipe(cachedCoefficient, RegistryManager.METAL_COEFFICIENT.get(), context, level);

		double metalMultiplier = cachedCoefficient == null ? 0.0 : cachedCoefficient.getCoefficient(context);
		double totalMult = BASE_MULTIPLIER;
		for (Direction facing : Misc.horizontals) {
			BlockState state = level.getBlockState(worldPosition.below().relative(facing));
			if (state.getBlock() == Blocks.LAVA || state.getBlock() == Blocks.FIRE) {
				totalMult += PER_BLOCK_MULTIPLIER * metalMultiplier;
			}
		}
		return totalMult;
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, PressureRefineryBottomBlockEntity blockEntity) {
		blockEntity.upgrades = UpgradeUtil.getUpgrades(level, pos, Misc.horizontals);
		UpgradeUtil.verifyUpgrades(blockEntity, blockEntity.upgrades);
		if(UpgradeUtil.doTick(blockEntity, blockEntity.upgrades))
			return;

		if (!blockEntity.inventory.getStackInSlot(0).isEmpty()) {
			BlockEntity tile = level.getBlockEntity(pos.above());
			if (blockEntity.tank.getFluid() != null && blockEntity.tank.getFluid().getFluid().is(FluidTags.WATER) && blockEntity.tank.getFluidAmount() >= FLUID_CONSUMED) {
				boolean cancel = UpgradeUtil.doWork(blockEntity, blockEntity.upgrades);

				if (!cancel && tile instanceof PressureRefineryTopBlockEntity) {
					PressureRefineryTopBlockEntity top = (PressureRefineryTopBlockEntity) tile;
					blockEntity.progress++;
					if (blockEntity.progress > UpgradeUtil.getWorkTime(blockEntity, PROCESS_TIME, blockEntity.upgrades)) {
						blockEntity.progress = 0;
						if (blockEntity.inventory != null) {
							RecipeWrapper wrapper = new RecipeWrapper(blockEntity.inventory);
							blockEntity.cachedRecipe = Misc.getRecipe(blockEntity.cachedRecipe, RegistryManager.EMBER_ACTIVATION.get(), wrapper, level);
							if (blockEntity.cachedRecipe != null) {
								double emberValue = blockEntity.cachedRecipe.getOutput(wrapper) * blockEntity.getMultiplier();
								double ember = UpgradeUtil.getTotalEmberProduction(blockEntity, emberValue, blockEntity.upgrades);
								if ((ember > 0 || emberValue == 0) && top.capability.getEmber() + ember <= top.capability.getEmberCapacity()) {
									level.playSound(null, pos, EmbersSounds.PRESSURE_REFINERY.get(), SoundSource.BLOCKS, 1.0f, 1.0f);

									if (level instanceof ServerLevel serverLevel) {
										serverLevel.sendParticles(new GlowParticleOptions(GlowParticleOptions.EMBER_COLOR, new Vec3(0, 0.65f, 0), 4.7f), pos.getX() + 0.5f, pos.getY() + 1.5f, pos.getZ() + 0.5f, 80, 0.1, 0.1, 0.1, 1.0);
										serverLevel.sendParticles(new SmokeParticleOptions(SmokeParticleOptions.SMOKE_COLOR, 5.0f), pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, 20, 0.1, 0.1, 0.1, 1.0);
									}
									UpgradeUtil.throwEvent(blockEntity, new EmberEvent(blockEntity, EmberEvent.EnumType.PRODUCE, ember), blockEntity.upgrades);
									top.capability.addAmount(ember, true);

									//the recipe is responsible for taking items from the inventory
									blockEntity.cachedRecipe.process(wrapper);
									blockEntity.tank.drain(FLUID_CONSUMED, FluidAction.EXECUTE);
								}
							}
						}
					}
					blockEntity.setChanged();
				}
			}
		}
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (!this.remove && side != Direction.DOWN && cap == ForgeCapabilities.ITEM_HANDLER) {
			return ForgeCapabilities.ITEM_HANDLER.orEmpty(cap, holder);
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		holder.invalidate();
	}

	@Override
	public void addDialInformation(Direction facing, List<String> information, String dialType) {
		if(EmberDialBlock.DIAL_TYPE.equals(dialType)) {
			DecimalFormat multiplierFormat = DecimalFormats.getDecimalFormat(Embers.MODID + ".decimal_format.ember_multiplier");
			double multiplier = getMultiplier();
			information.add(I18n.get(Embers.MODID + ".tooltip.dial.ember_multiplier", multiplierFormat.format(multiplier)));
		}
		UpgradeUtil.throwEvent(this, new DialInformationEvent(this, information, dialType), upgrades);
	}

	@Override
	public boolean hasCapabilityDescription(Capability<?> capability) {
		return capability == ForgeCapabilities.ITEM_HANDLER;
	}

	@Override
	public void addCapabilityDescription(List<String> strings, Capability<?> capability, Direction facing) {
		if (capability == ForgeCapabilities.ITEM_HANDLER)
			strings.add(IExtraCapabilityInformation.formatCapability(EnumIOType.INPUT, Embers.MODID + ".tooltip.goggles.item",I18n.get(Embers.MODID + ".tooltip.goggles.item.ember")));
		if(capability == ForgeCapabilities.FLUID_HANDLER)
			strings.add(IExtraCapabilityInformation.formatCapability(EnumIOType.INPUT,"embers.tooltip.goggles.fluid",I18n.get(Embers.MODID + ".tooltip.goggles.fluid.water")));
	}
}
