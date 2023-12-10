package com.rekindled.embers.block;

import javax.annotation.Nullable;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.AutomaticHammerBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AutomaticHammerBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

	public static final VoxelShape HAMMER_NORTH_AABB = Shapes.or(Block.box(0,0,12,16,16,16), Block.box(6,6,2,10,10,6), Block.box(4,4,10,12,12,12), Block.box(5,5,6,11,11,10), Block.box(2,9,8,7,14,12), Block.box(9,9,8,14,14,12), Block.box(2,2,8,7,7,12), Block.box(9,2,8,14,7,12));
	public static final VoxelShape HAMMER_EAST_AABB = Shapes.or(Block.box(0,0,0,4,16,16), Block.box(10,6,6,14,10,10), Block.box(4,4,4,6,12,12), Block.box(6,5,5,10,11,11), Block.box(4,9,2,8,14,7), Block.box(4,9,9,8,14,14), Block.box(4,2,2,8,7,7), Block.box(4,2,9,8,7,14));
	public static final VoxelShape HAMMER_SOUTH_AABB = Shapes.or(Block.box(0,0,0,16,16,4), Block.box(6,6,10,10,10,14), Block.box(4,4,4,12,12,6), Block.box(5,5,6,11,11,10), Block.box(9,9,4,14,14,8),Block.box(2,9,4,7,14,8), Block.box(9,2,4,14,7,8), Block.box(2,2,4,7,7,8));
	public static final VoxelShape HAMMER_WEST_AABB = Shapes.or(Block.box(12,0,0,16,16,16), Block.box(2,6,6,6,10,10), Block.box(10,4,4,12,12,12), Block.box(6,5,5,10,11,11), Block.box(8,9,9,12,14,14), Block.box(8,9,2,12,14,7), Block.box(8,2,9,12,7,14), Block.box(8,2,2,12,7,7));

	public AutomaticHammerBlock(Properties properties) {
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
			return HAMMER_EAST_AABB;
		case WEST:
			return HAMMER_WEST_AABB;
		case SOUTH:
			return HAMMER_SOUTH_AABB;
		case NORTH:
		default:
			return HAMMER_NORTH_AABB;
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return RegistryManager.AUTOMATIC_HAMMER_ENTITY.get().create(pPos, pState);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		return pLevel.isClientSide ? createTickerHelper(pBlockEntityType, RegistryManager.AUTOMATIC_HAMMER_ENTITY.get(), AutomaticHammerBlockEntity::clientTick) : createTickerHelper(pBlockEntityType, RegistryManager.AUTOMATIC_HAMMER_ENTITY.get(), AutomaticHammerBlockEntity::serverTick);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		Direction direction;
		if (pContext.getClickedFace().getAxis() != Axis.Y) {
			direction = pContext.getClickedFace();
		} else {
			direction = pContext.getHorizontalDirection().getOpposite();
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
}
