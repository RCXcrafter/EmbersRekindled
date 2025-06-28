package com.rekindled.embers.block;

import javax.annotation.Nullable;

import com.rekindled.embers.RegistryManager;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HeatExchangerBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

	public static final VoxelShape RADIATOR_NORTH_AABB = Shapes.or(Block.box(4,0,2,12,13,4), Block.box(4,0,10,12,13,12), Block.box(9,9,0,11,11,2), Block.box(9,2,0,11,4,2), Block.box(5,9,0,7,11,2), Block.box(5,2,0,7,4,2), Block.box(5,10,12,11,12,13), Block.box(5,7,12,11,9,13), Block.box(5,4,12,11,6,13), Block.box(5,1,12,11,3,13), Block.box(5,1,4,11,12,10));
	public static final VoxelShape RADIATOR_SOUTH_AABB = Shapes.or(Block.box(4,0,12,12,13,14), Block.box(4,0,4,12,13,6), Block.box(5,9,14,7,11,16), Block.box(5,2,14,7,4,16), Block.box(9,9,14,11,11,16), Block.box(9,2,14,11,4,16), Block.box(5,10,3,11,12,4), Block.box(5,7,3,11,9,4), Block.box(5,4,3,11,6,4), Block.box(5,1,3,11,3,4), Block.box(5,1,6,11,12,12));
	public static final VoxelShape RADIATOR_WEST_AABB = Shapes.or(Block.box(2,0,4,4,13,12), Block.box(10,0,4,12,13,12), Block.box(0,9,5,2,11,7), Block.box(0,2,5,2,4,7), Block.box(0,9,9,2,11,11), Block.box(0,2,9,2,4,11), Block.box(12,10,5,13,12,11), Block.box(12,7,5,13,9,11), Block.box(12,4,5,13,6,11), Block.box(12,1,5,13,3,11), Block.box(4,1,5,10,12,11));
	public static final VoxelShape RADIATOR_EAST_AABB = Shapes.or(Block.box(12,0,4,14,13,12), Block.box(4,0,4,6,13,12), Block.box(14,9,9,16,11,11), Block.box(14,2,9,16,4,11), Block.box(14,9,5,16,11,7), Block.box(14,2,5,16,4,7), Block.box(3,10,5,4,12,11), Block.box(3,7,5,4,9,11), Block.box(3,4,5,4,6,11), Block.box(3,1,5,4,3,11), Block.box(6,1,5,12,12,11));

	public HeatExchangerBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.WATERLOGGED, false).setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
	}

	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		switch (state.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
		case EAST:
			return RADIATOR_EAST_AABB;
		case WEST:
			return RADIATOR_WEST_AABB;
		case SOUTH:
			return RADIATOR_SOUTH_AABB;
		case NORTH:
		default:
			return RADIATOR_NORTH_AABB;
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return RegistryManager.HEAT_EXCHANGER_ENTITY.get().create(pPos, pState);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		Direction direction;
		if (pContext.getClickedFace().getAxis() != Axis.Y) {
			direction = pContext.getClickedFace().getOpposite();
		} else {
			direction = pContext.getHorizontalDirection();
		}
		return super.getStateForPlacement(pContext).setValue(BlockStateProperties.HORIZONTAL_FACING, direction).setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(pContext.getLevel().getFluidState(pContext.getClickedPos()).getType() == Fluids.WATER));
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
		if (pState.getValue(BlockStateProperties.WATERLOGGED)) {
			pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
		}
		return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(BlockStateProperties.WATERLOGGED, BlockStateProperties.HORIZONTAL_FACING);
	}

	@Override
	public FluidState getFluidState(BlockState pState) {
		return pState.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(BlockStateProperties.HORIZONTAL_FACING, rotation.rotate(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.setValue(BlockStateProperties.HORIZONTAL_FACING, mirror.mirror(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
	}
}
