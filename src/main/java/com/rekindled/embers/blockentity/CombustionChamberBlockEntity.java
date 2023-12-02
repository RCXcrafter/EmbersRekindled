package com.rekindled.embers.blockentity;

import java.util.List;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.tile.IExtraCapabilityInformation;
import com.rekindled.embers.recipe.CatalysisCombustionContext;
import com.rekindled.embers.recipe.ICatalysisCombustionRecipe;
import com.rekindled.embers.util.Misc;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class CombustionChamberBlockEntity extends BlockEntity implements IExtraCapabilityInformation {

	public static ItemStack machine = new ItemStack(RegistryManager.COMBUSTION_CHAMBER_ITEM.get());
	int progress = 0;
	double multiplier = 0;
	public ItemStackHandler inventory = new ItemStackHandler(1) {
		@Override
		protected void onContentsChanged(int slot) {
			CombustionChamberBlockEntity.this.setChanged();
		}
	};
	public LazyOptional<IItemHandler> holder = LazyOptional.of(() -> inventory);
	public ICatalysisCombustionRecipe cachedRecipe = null;

	public CombustionChamberBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.COMBUSTION_CHAMBER_ENTITY.get(), pPos, pBlockState);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		inventory.deserializeNBT(nbt.getCompound("inventory"));
		progress = nbt.getInt("progress");
		multiplier = nbt.getDouble("multiplier");
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		nbt.put("inventory", inventory.serializeNBT());
		nbt.putInt("progress", progress);
		nbt.putDouble("multiplier", multiplier);
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

	public static void serverTick(Level level, BlockPos pos, BlockState state, CombustionChamberBlockEntity blockEntity) {
		if (blockEntity.progress > 0) {
			blockEntity.progress --;
			if (blockEntity.progress == 0) {
				blockEntity.multiplier = 0;
			}
			blockEntity.setChanged();
		}
		if (blockEntity.progress == 0 && !blockEntity.inventory.getStackInSlot(0).isEmpty()) {
			CatalysisCombustionContext wrapper = new CatalysisCombustionContext(blockEntity.inventory, machine);
			blockEntity.cachedRecipe = Misc.getRecipe(blockEntity.cachedRecipe, RegistryManager.CATALYSIS_COMBUSTION.get(), wrapper, level);
			if (blockEntity.cachedRecipe != null) {
				blockEntity.multiplier = blockEntity.cachedRecipe.getmultiplier(wrapper);
				blockEntity.progress = blockEntity.cachedRecipe.getBurnTIme(wrapper);

				//the recipe is responsible for taking items from the inventory
				blockEntity.cachedRecipe.process(wrapper);
			}
		}
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (!this.remove && cap == ForgeCapabilities.ITEM_HANDLER) {
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
			strings.add(IExtraCapabilityInformation.formatCapability(EnumIOType.INPUT, Embers.MODID + ".tooltip.goggles.item",I18n.get(Embers.MODID + ".tooltip.goggles.item.combustion")));
	}
}
