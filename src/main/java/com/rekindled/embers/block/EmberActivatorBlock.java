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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EmberActivatorBlock extends DoubleTallMachineBlock {

	protected static final VoxelShape BASE_AABB = Shapes.or(Block.box(0,0,0,4,4,4),Block.box(12,0,0,16,4,4),Block.box(12,0,12,16,4,16),Block.box(0,0,12,4,4,16),Block.box(1,0,1,15,3,15),Block.box(2,3,2,14,7,14),Block.box(4,7,4,12,14,12),Block.box(3,14,3,13,16,13),Block.box(6,6,0,10,10,16),Block.box(0,6,6,16,10,10));
	protected static final VoxelShape TOP_AABB = Shapes.or(Block.box(4,0,4,12,2,12),Block.box(2,2,2,14,4,14),Block.box(3,4,3,5,16,5),Block.box(7,4,3,9,16,5),Block.box(11,4,3,13,16,5),Block.box(11,4,7,13,16,9),Block.box(11,4,11,13,16,13),Block.box(7,4,11,9,16,13),Block.box(3,4,11,5,16,13),Block.box(3,4,7,5,16,9),Block.box(3,6,5,5,8,11),Block.box(5,6,3,11,8,5),Block.box(11,6,5,13,8,11),Block.box(5,6,11,11,8,13),Block.box(5,10,11,11,12,13),Block.box(11,10,5,13,12,11),Block.box(5,10,3,11,12,5),Block.box(3,10,5,5,12,11),Block.box(5,3,5,11,5,11),Block.box(13,6,6,15,10,10),Block.box(1,6,6,3,10,10),Block.box(6,6,1,10,10,3),Block.box(6,6,13,10,10,15));
	protected static final VoxelShape TOP_INTERACTION = Block.box(1,4,1,15,16,15);

	public EmberActivatorBlock(Properties properties, SoundType topSound) {
		super(properties, topSound);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? BASE_AABB : TOP_AABB;
	}

	@Override
	public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
		return state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? Shapes.block() : TOP_INTERACTION;
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
