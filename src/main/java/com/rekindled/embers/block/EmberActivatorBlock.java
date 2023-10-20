package com.rekindled.embers.block;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.EmberActivatorBottomBlockEntity;
import com.rekindled.embers.blockentity.EmberActivatorTopBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EmberActivatorBlock extends DoubleTallMachineBlock {

	protected static final VoxelShape BASE_AABB = Shapes.or(Block.box(0,0,0,16,4,16), Block.box(2,0,2,14,16,14));
	protected static final VoxelShape TOP_AABB = Shapes.or(Shapes.joinUnoptimized(Block.box(3,4,3,13,16,13), Block.box(5,5,5,11,16,11), BooleanOp.ONLY_FIRST), Block.box(2,2,2,14,4,14), Block.box(4,0,4,12,2,12));

	public EmberActivatorBlock(Properties properties, SoundType topSound) {
		super(properties, topSound);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? BASE_AABB : TOP_AABB;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		if (pState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER)
			return RegistryManager.EMBER_ACTIVATOR_BOTTOM_ENTITY.get().create(pPos, pState);
		return RegistryManager.EMBER_ACTIVATOR_TOP_ENTITY.get().create(pPos, pState);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		if (pState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER)
			return pLevel.isClientSide ? createTickerHelper(pBlockEntityType, RegistryManager.EMBER_ACTIVATOR_BOTTOM_ENTITY.get(), EmberActivatorBottomBlockEntity::clientTick) : createTickerHelper(pBlockEntityType, RegistryManager.EMBER_ACTIVATOR_BOTTOM_ENTITY.get(), EmberActivatorBottomBlockEntity::serverTick);
		return pLevel.isClientSide ? createTickerHelper(pBlockEntityType, RegistryManager.EMBER_ACTIVATOR_TOP_ENTITY.get(), EmberActivatorTopBlockEntity::clientTick) : null;
	}
}
