package com.rekindled.embers.block;

import javax.annotation.Nullable;

import com.rekindled.embers.RegistryManager;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

public class HeatInsulationBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

	protected static final VoxelShape UP_AABB = Shapes.or(Block.box(2,13,2,14,15,14), Block.box(0,12,0,4,16,4), Block.box(12,12,0,16,16,4), Block.box(12,12,12,16,16,16), Block.box(0,12,12,4,16,16), Block.box(1,4,1,15,13,15), Block.box(0,0,0,16,4,16));
	protected static final VoxelShape DOWN_AABB = Shapes.or(Block.box(2,1,2,14,3,14), Block.box(0,0,12,4,4,16), Block.box(12,0,12,16,4,16), Block.box(12,0,0,16,4,4), Block.box(0,0,0,4,4,4), Block.box(1,3,1,15,12,15), Block.box(0,12,0,16,16,16));
	protected static final VoxelShape NORTH_AABB = Shapes.or(Block.box(2,2,1,14,14,3), Block.box(0,0,0,4,4,4), Block.box(12,0,0,16,4,4), Block.box(12,12,0,16,16,4), Block.box(0,12,0,4,16,4), Block.box(1,1,3,15,15,12), Block.box(0,0,12,16,16,16));
	protected static final VoxelShape SOUTH_AABB = Shapes.or(Block.box(2,2,13,14,14,15), Block.box(12,0,12,16,4,16), Block.box(0,0,12,4,4,16), Block.box(0,12,12,4,16,16), Block.box(12,12,12,16,16,16), Block.box(1,1,4,15,15,13), Block.box(0,0,0,16,16,4));
	protected static final VoxelShape WEST_AABB = Shapes.or(Block.box(1,2,2,3,14,14), Block.box(0,0,12,4,4,16), Block.box(0,0,0,4,4,4), Block.box(0,12,0,4,16,4), Block.box(0,12,12,4,16,16), Block.box(3,1,1,12,15,15), Block.box(12,0,0,16,16,16));
	protected static final VoxelShape EAST_AABB = Shapes.or(Block.box(13,2,2,15,14,14), Block.box(12,0,0,16,4,4), Block.box(12,0,12,16,4,16), Block.box(12,12,12,16,16,16), Block.box(12,12,0,16,16,4), Block.box(4,1,1,13,15,15), Block.box(0,0,0,4,16,16));

	public HeatInsulationBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.FACING, Direction.UP).setValue(BlockStateProperties.WATERLOGGED, false));
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		switch (pState.getValue(BlockStateProperties.FACING)) {
		case UP:
			return UP_AABB;
		case DOWN:
			return DOWN_AABB;
		case EAST:
			return EAST_AABB;
		case WEST:
			return WEST_AABB;
		case SOUTH:
			return SOUTH_AABB;
		case NORTH:
		default:
			return NORTH_AABB;
		}
	}

	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		for (Direction direction : pContext.getNearestLookingDirections()) {
			BlockState blockstate = this.defaultBlockState().setValue(BlockStateProperties.FACING, direction.getOpposite());
			return blockstate.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(pContext.getLevel().getFluidState(pContext.getClickedPos()).getType() == Fluids.WATER));
		}
		return null;
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
		pBuilder.add(BlockStateProperties.FACING).add(BlockStateProperties.WATERLOGGED);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return RegistryManager.HEAT_INSULATION_ENTITY.get().create(pPos, pState);
	}

	@Override
	public FluidState getFluidState(BlockState pState) {
		return pState.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(BlockStateProperties.FACING, rotation.rotate(state.getValue(BlockStateProperties.FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.setValue(BlockStateProperties.FACING, mirror.mirror(state.getValue(BlockStateProperties.FACING)));
	}
}
