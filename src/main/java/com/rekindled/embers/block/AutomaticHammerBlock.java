package com.rekindled.embers.block;

import javax.annotation.Nullable;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.AutomaticHammerBlockEntity;
import com.rekindled.embers.util.Misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
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

	public static final VoxelShape HAMMER_NORTH_AABB = Shapes.or(Block.box(0,0,12,16,4,16),Block.box(0,4,12,4,12,16),Block.box(12,4,12,16,12,16),Block.box(0,12,12,16,16,16),Block.box(6,6,2,10,10,16),Block.box(4,4,10,12,12,15),Block.box(5,5,6,11,11,10),Block.box(2,9,8,7,14,12),Block.box(9,9,8,14,14,12),Block.box(2,2,8,7,7,12),Block.box(9,2,8,14,7,12));
	public static final VoxelShape HAMMER_EAST_AABB = Misc.rotateVoxelShape(Direction.NORTH, Direction.EAST, HAMMER_NORTH_AABB);
	public static final VoxelShape HAMMER_SOUTH_AABB = Misc.rotateVoxelShape(Direction.NORTH, Direction.SOUTH, HAMMER_NORTH_AABB);
	public static final VoxelShape HAMMER_WEST_AABB = Misc.rotateVoxelShape(Direction.NORTH, Direction.WEST, HAMMER_NORTH_AABB);
	public static final VoxelShape NORTH_INTERACTION = Block.box(2,2,2,14,14,16);
	public static final VoxelShape EAST_INTERACTION = Block.box(0,2,2,14,14,14);
	public static final VoxelShape SOUTH_INTERACTION = Block.box(2,2,0,14,14,14);
	public static final VoxelShape WEST_INTERACTION = Block.box(2,2,2,16,14,14);

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
	public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
		switch (state.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
		case EAST:
			return EAST_INTERACTION;
		case WEST:
			return WEST_INTERACTION;
		case SOUTH:
			return SOUTH_INTERACTION;
		case NORTH:
		default:
			return NORTH_INTERACTION;
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

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(BlockStateProperties.HORIZONTAL_FACING, rotation.rotate(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.setValue(BlockStateProperties.HORIZONTAL_FACING, mirror.mirror(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
	}
}
