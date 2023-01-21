package com.rekindled.embers.blockentity;

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
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.particle.GlowParticleOptions;
import com.rekindled.embers.particle.SmokeParticleOptions;
import com.rekindled.embers.recipe.EmberActivationRecipe;
import com.rekindled.embers.recipe.SingleItemContainer;
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

public class EmberActivatorBottomBlockEntity extends BlockEntity implements IExtraDialInformation, IExtraCapabilityInformation {

	public static final int PROCESS_TIME = 40;
	int progress = -1;
	public ItemStackHandler inventory = new ItemStackHandler(1) {
		@Override
		protected void onContentsChanged(int slot) {
			EmberActivatorBottomBlockEntity.this.setChanged();
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

	public EmberActivatorBottomBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.EMBER_ACTIVATOR_BOTTOM_ENTITY.get(), pPos, pBlockState);
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

	public static void serverTick(Level level, BlockPos pos, BlockState state, EmberActivatorBottomBlockEntity blockEntity) {
		blockEntity.upgrades = UpgradeUtil.getUpgrades(level, pos, Misc.horizontals);
		UpgradeUtil.verifyUpgrades(blockEntity, blockEntity.upgrades);
		if(UpgradeUtil.doTick(blockEntity, blockEntity.upgrades))
			return;

		if (!blockEntity.inventory.getStackInSlot(0).isEmpty()) {
			BlockEntity tile = level.getBlockEntity(pos.above());
			boolean cancel = UpgradeUtil.doWork(blockEntity, blockEntity.upgrades);

			if (!cancel && tile instanceof EmberActivatorTopBlockEntity) {
				EmberActivatorTopBlockEntity top = (EmberActivatorTopBlockEntity) tile;
				blockEntity.progress++;
				if (blockEntity.progress > UpgradeUtil.getWorkTime(blockEntity, PROCESS_TIME, blockEntity.upgrades)) {
					blockEntity.progress = 0;
					if (blockEntity.inventory != null) {
						RecipeWrapper wrapper = new RecipeWrapper(blockEntity.inventory);
						List<EmberActivationRecipe> recipes = level.getRecipeManager().getRecipesFor(RegistryManager.EMBER_ACTIVATION.get(), wrapper, level);
						double emberValue = recipes.get(0).getOutput(wrapper);
						double ember = UpgradeUtil.getTotalEmberProduction(blockEntity, emberValue, blockEntity.upgrades);
						if ((ember > 0 || emberValue == 0) && top.capability.getEmber() + ember <= top.capability.getEmberCapacity()) {
							level.playSound(null, pos, EmbersSounds.ACTIVATOR.get(), SoundSource.BLOCKS, 1.0f, 1.0f);

							if (level instanceof ServerLevel serverLevel) {
								serverLevel.sendParticles(new GlowParticleOptions(GlowParticleOptions.EMBER_COLOR, new Vec3(0, 0.65f, 0), 4.7f), pos.getX() + 0.5f, pos.getY() + 1.5f, pos.getZ() + 0.5f, 80, 0.1, 0.1, 0.1, 1.0);
								serverLevel.sendParticles(new SmokeParticleOptions(SmokeParticleOptions.SMOKE_COLOR, 5.0f), pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, 20, 0.1, 0.1, 0.1, 1.0);
							}
							UpgradeUtil.throwEvent(blockEntity, new EmberEvent(blockEntity, EmberEvent.EnumType.PRODUCE, ember), blockEntity.upgrades);
							top.capability.addAmount(ember, true);

							//the recipe is responsible for taking items from the inventory
							recipes.get(0).process(wrapper);
						}
					}
				}
				blockEntity.setChanged();
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
	public boolean hasCapabilityDescription(Capability<?> capability) {
		return capability == ForgeCapabilities.ITEM_HANDLER;
	}

	@Override
	public void addCapabilityDescription(List<String> strings, Capability<?> capability, Direction facing) {
		if (capability == ForgeCapabilities.ITEM_HANDLER)
			strings.add(IExtraCapabilityInformation.formatCapability(EnumIOType.INPUT, Embers.MODID + ".tooltip.goggles.item",I18n.get(Embers.MODID + ".tooltip.goggles.item.ember")));
	}

	@Override
	public void addDialInformation(Direction facing, List<String> information, String dialType) {
		UpgradeUtil.throwEvent(this, new DialInformationEvent(this, information, dialType), upgrades);
	}
}
