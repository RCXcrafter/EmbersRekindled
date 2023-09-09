package com.rekindled.embers.blockentity;

import java.util.List;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.tile.IExtraCapabilityInformation;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.FluidHandlerBlockEntity;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class MechanicalPumpTopBlockEntity extends FluidHandlerBlockEntity implements IExtraCapabilityInformation {

	public static int capacity = FluidType.BUCKET_VOLUME * 8;

	public MechanicalPumpTopBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.MECHANICAL_PUMP_TOP_ENTITY.get(), pPos, pBlockState);
		tank = new FluidTank(capacity) {
			@Override
			public void onContentsChanged() {
				MechanicalPumpTopBlockEntity.this.setChanged();
			}
		};
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

	public int getCapacity(){
		return tank.getCapacity();
	}

	public FluidStack getFluidStack() {
		return tank.getFluid();
	}

	public FluidTank getTank() {
		return tank;
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
		return capability == ForgeCapabilities.FLUID_HANDLER;
	}

	@Override
	public void addCapabilityDescription(List<String> strings, Capability<?> capability, Direction facing) {
		if (capability == ForgeCapabilities.FLUID_HANDLER)
			strings.add(IExtraCapabilityInformation.formatCapability(EnumIOType.OUTPUT, Embers.MODID + ".tooltip.goggles.fluid", I18n.get(Embers.MODID + ".tooltip.goggles.fluid.water")));
	}
}
