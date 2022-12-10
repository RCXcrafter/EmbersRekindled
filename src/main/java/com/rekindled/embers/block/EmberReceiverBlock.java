package com.rekindled.embers.block;

import javax.annotation.Nullable;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.EmberReceiverBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EmberReceiverBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

	protected static final VoxelShape UP_AABB = Shapes.box(0.25,0,0.25,0.75,0.5,0.75);
	protected static final VoxelShape DOWN_AABB = Shapes.box(0.25,0.5,0.25,0.75,1.0,0.75);
	protected static final VoxelShape NORTH_AABB = Shapes.box(0.25,0.25,0.5,0.75,0.75,1.0);
	protected static final VoxelShape SOUTH_AABB = Shapes.box(0.25,0.25,0,0.75,0.75,0.5);
	protected static final VoxelShape WEST_AABB = Shapes.box(0.5,0.25,0.25,1.0,0.75,0.75);
	protected static final VoxelShape EAST_AABB = Shapes.box(0.0,0.25,0.25,0.5,0.75,0.75);

	public static final DirectionProperty FACING = BlockStateProperties.FACING;

	public EmberReceiverBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP).setValue(BlockStateProperties.WATERLOGGED, false));
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		switch (pState.getValue(FACING)) {
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

	@Override
	public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
		return canAttach(pLevel, pPos, pState.getValue(FACING).getOpposite());
	}

	public static boolean canAttach(LevelReader pReader, BlockPos pPos, Direction pDirection) {
		return !pReader.getBlockState(pPos.relative(pDirection)).isAir();
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		for(Direction direction : pContext.getNearestLookingDirections()) {
			BlockState blockstate = this.defaultBlockState().setValue(FACING, direction.getOpposite());
			if (blockstate.canSurvive(pContext.getLevel(), pContext.getClickedPos())) {
				return blockstate.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(pContext.getLevel().getFluidState(pContext.getClickedPos()).getType() == Fluids.WATER));
			}
		}
		return null;
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
		if (pState.getValue(BlockStateProperties.WATERLOGGED)) {
			pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
		}
		return pState.getValue(FACING).getOpposite() == pFacing && !pState.canSurvive(pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
	}

	@Override
	public BlockState rotate(BlockState pState, Rotation pRot) {
		return pState.setValue(FACING, pRot.rotate(pState.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState pState, Mirror pMirror) {
		return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(FACING).add(BlockStateProperties.WATERLOGGED);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return RegistryManager.EMBER_RECEIVER_ENTITY.get().create(pPos, pState);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		return pLevel.isClientSide ? null : createTickerHelper(pBlockEntityType, RegistryManager.EMBER_RECEIVER_ENTITY.get(), EmberReceiverBlockEntity::serverTick);
	}

	@Override
	public FluidState getFluidState(BlockState pState) {
		return pState.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
	}
}
