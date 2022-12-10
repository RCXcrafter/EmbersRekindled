package com.rekindled.embers.block;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.ItemPipeBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ItemPipeBlock extends PipeBlockBase {

	public ItemPipeBlock(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public boolean connected(Direction direction, BlockState state) {
		return false;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return RegistryManager.ITEM_PIPE_ENTITY.get().create(pPos, pState);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		return pLevel.isClientSide ? null : createTickerHelper(pBlockEntityType, RegistryManager.ITEM_PIPE_ENTITY.get(), ItemPipeBlockEntity::serverTick);
	}
}
