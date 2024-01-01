package com.rekindled.embers.blockentity;

import java.util.List;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.tile.IExtraCapabilityInformation;
import com.rekindled.embers.datagen.EmbersItemTags;
import com.rekindled.embers.upgrade.MnemonicInscriberUpgrade;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class MnemonicInscriberBlockEntity extends BlockEntity implements IExtraCapabilityInformation {

	public ItemStackHandler inventory = new ItemStackHandler(1) {
		@Override
		public int getSlotLimit(int slot) {
			return 1;
		}

		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			return stack.is(EmbersItemTags.INSCRIBABLE_PAPER);
		}

		@Override
		protected void onContentsChanged(int slot) {
			MnemonicInscriberBlockEntity.this.setChanged();
		}
	};
	public LazyOptional<IItemHandler> holder = LazyOptional.of(() -> inventory);

	public MnemonicInscriberUpgrade upgrade;

	public MnemonicInscriberBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.MNEMONIC_INSCRIBER_ENTITY.get(), pPos, pBlockState);
		upgrade = new MnemonicInscriberUpgrade(this);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		inventory.deserializeNBT(nbt.getCompound("inventory"));
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		nbt.put("inventory", inventory.serializeNBT());
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

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (!this.remove && (level.getBlockState(worldPosition).hasProperty(BlockStateProperties.FACING) || side == null)) {
			Direction facing = side == null ? null : level.getBlockState(worldPosition).getValue(BlockStateProperties.FACING);
			if (cap == ForgeCapabilities.ITEM_HANDLER && (side == null || side.getOpposite() != facing)) {
				return ForgeCapabilities.ITEM_HANDLER.orEmpty(cap, holder);
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

	@Override
	public boolean hasCapabilityDescription(Capability<?> capability) {
		return capability == ForgeCapabilities.ITEM_HANDLER;
	}

	@Override
	public void addCapabilityDescription(List<String> strings, Capability<?> capability, Direction facing) {
		if (capability == ForgeCapabilities.ITEM_HANDLER)
			strings.add(IExtraCapabilityInformation.formatCapability(EnumIOType.INPUT, Embers.MODID + ".tooltip.goggles.item", I18n.get(Embers.MODID + ".tooltip.goggles.item.paper")));
	}
}
