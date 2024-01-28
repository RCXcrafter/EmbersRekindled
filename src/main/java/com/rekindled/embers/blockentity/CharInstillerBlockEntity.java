package com.rekindled.embers.blockentity;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.upgrade.CharInstillerUpgrade;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class CharInstillerBlockEntity extends BlockEntity {

	public CharInstillerUpgrade upgrade;

	public CharInstillerBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.CHAR_INSTILLER_ENTITY.get(), pPos, pBlockState);
		upgrade = new CharInstillerUpgrade(this);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (!this.remove && level.getBlockState(worldPosition).hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
			if (cap == EmbersCapabilities.UPGRADE_PROVIDER_CAPABILITY && side == level.getBlockState(worldPosition).getValue(BlockStateProperties.HORIZONTAL_FACING)) {
				return upgrade.getCapability(cap, side);
			}
		}
		return super.getCapability(cap, side);
	}
}
