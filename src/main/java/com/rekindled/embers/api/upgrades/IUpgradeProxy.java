package com.rekindled.embers.api.upgrades;

import java.util.List;

import com.rekindled.embers.api.tile.IUpgradeable;
import com.rekindled.embers.blockentity.MechanicalCoreBlockEntity.BlockEntityDirection;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IUpgradeProxy extends IUpgradeable {
	void collectUpgrades(List<UpgradeContext> upgrades, int distanceLeft);
	boolean isSocket(Direction facing);
	boolean isProvider(Direction facing);
	public BlockEntityDirection getAttachedMultiblock(int distanceLeft);
	public BlockEntity getAttachedBlockEntity(int distanceLeft);
}
