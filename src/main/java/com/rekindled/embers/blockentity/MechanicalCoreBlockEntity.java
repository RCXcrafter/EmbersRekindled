package com.rekindled.embers.blockentity;

import java.util.List;

import com.rekindled.embers.ConfigManager;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.tile.IExtraCapabilityInformation;
import com.rekindled.embers.api.tile.IExtraDialInformation;
import com.rekindled.embers.api.tile.IUpgradeable;
import com.rekindled.embers.api.upgrades.IUpgradeProxy;
import com.rekindled.embers.api.upgrades.UpgradeContext;
import com.rekindled.embers.api.upgrades.UpgradeUtil;
import com.rekindled.embers.util.Misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class MechanicalCoreBlockEntity extends BlockEntity implements IExtraDialInformation, IExtraCapabilityInformation, IUpgradeProxy {

	public MechanicalCoreBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.MECHANICAL_CORE_ENTITY.get(), pPos, pBlockState);
	}

	public BlockEntityDirection getAttachedMultiblock(int distanceLeft) {
		if (distanceLeft < 1) {
			return null;
		}
		BlockPos sidePos = worldPosition.relative(getAttachedSide());
		if (Misc.isSideProxyable(level.getBlockState(sidePos), level.getBlockEntity(sidePos), getFace())) {
			return new BlockEntityDirection(level.getBlockEntity(sidePos), getFace());
		}
		if (level.getBlockEntity(sidePos) instanceof IUpgradeProxy proxy) {
			return proxy.getAttachedMultiblock(distanceLeft - 1);
		}
		return null;
	}

	public BlockEntity getAttachedBlockEntity(int distanceLeft) {
		if (distanceLeft < 1) {
			return null;
		}
		BlockPos sidePos = worldPosition.relative(getAttachedSide());
		if (Misc.isSideProxyable(level.getBlockState(sidePos), level.getBlockEntity(sidePos), getFace())) {
			return level.getBlockEntity(sidePos);
		}
		if (level.getBlockEntity(sidePos) instanceof IUpgradeProxy proxy) {
			return proxy.getAttachedBlockEntity(distanceLeft - 1);
		}
		return null;
	}

	public Direction getAttachedSide() {
		return getFace().getOpposite();
	}

	public Direction getFace() {
		return getBlockState().getValue(BlockStateProperties.FACING);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		BlockEntityDirection multiblock = getAttachedMultiblock(ConfigManager.MAX_PROXY_DISTANCE.get());
		if (multiblock != null)
			return multiblock.blockEntity.getCapability(cap, multiblock.direction);
		return super.getCapability(cap, side);
	}

	@Override
	public boolean isSideUpgradeSlot(Direction face) {
		BlockEntityDirection multiblock = getAttachedMultiblock(ConfigManager.MAX_PROXY_DISTANCE.get());
		if (multiblock != null)
			return multiblock.blockEntity instanceof IUpgradeable upgradeable && upgradeable.isSideUpgradeSlot(multiblock.direction);
		return false;
	}

	@Override
	public void addDialInformation(Direction facing, List<Component> information, String dialType) {
		BlockEntityDirection multiblock = getAttachedMultiblock(ConfigManager.MAX_PROXY_DISTANCE.get());
		if (multiblock != null && multiblock.blockEntity instanceof IExtraDialInformation)
			((IExtraDialInformation) multiblock.blockEntity).addDialInformation(multiblock.direction, information, dialType);
	}

	@Override
	public boolean hasCapabilityDescription(Capability<?> capability) {
		BlockEntity multiblock = getAttachedBlockEntity(ConfigManager.MAX_PROXY_DISTANCE.get());
		if (multiblock instanceof IExtraCapabilityInformation)
			return ((IExtraCapabilityInformation) multiblock).hasCapabilityDescription(capability);
		return false;
	}

	@Override
	public void addCapabilityDescription(List<Component> strings, Capability<?> capability, Direction facing) {
		BlockEntityDirection multiblock = getAttachedMultiblock(ConfigManager.MAX_PROXY_DISTANCE.get());
		if (multiblock != null && multiblock.blockEntity instanceof IExtraCapabilityInformation)
			((IExtraCapabilityInformation) multiblock.blockEntity).addCapabilityDescription(strings, capability, multiblock.direction);
	}

	@Override
	public void addOtherDescription(List<Component> strings, Direction facing) {
		BlockEntityDirection multiblock = getAttachedMultiblock(ConfigManager.MAX_PROXY_DISTANCE.get());
		if (multiblock != null && multiblock.blockEntity instanceof IExtraCapabilityInformation)
			((IExtraCapabilityInformation) multiblock.blockEntity).addOtherDescription(strings, multiblock.direction);
	}

	@Override
	public void collectUpgrades(List<UpgradeContext> upgrades, int distanceLeft) {
		for (Direction facing : Direction.values()) {
			if (isSocket(facing))
				UpgradeUtil.collectUpgrades(level, worldPosition.relative(facing), facing.getOpposite(), upgrades, distanceLeft);
		}
	}

	@Override
	public boolean isSocket(Direction facing) {
		return facing != getAttachedSide();
	}

	@Override
	public boolean isProvider(Direction facing) {
		return facing == getAttachedSide();
	}

	public static class BlockEntityDirection {

		public BlockEntity blockEntity;
		public Direction direction;

		public BlockEntityDirection(BlockEntity blockEntity, Direction direction) {
			this.blockEntity = blockEntity;
			this.direction = direction;
		}
	}
}
